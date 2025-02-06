package com.beb.backend.service;

import com.beb.backend.common.ValidationRegexConstants;
import com.beb.backend.domain.Book;
import com.beb.backend.domain.Category;
import com.beb.backend.dto.externalApiDto.AladinBookItemDto;
import com.beb.backend.dto.externalApiDto.AladinBookSearchResponseDto;
import com.beb.backend.dto.externalApiDto.NaverBookItemDto;
import com.beb.backend.dto.externalApiDto.NaverBookSearchResponseDto;
import com.beb.backend.dto.responseDto.*;
import com.beb.backend.exception.BookException;
import com.beb.backend.exception.BookExceptionInfo;
import com.beb.backend.exception.OpenApiException;
import com.beb.backend.exception.OpenApiExceptionInfo;
import com.beb.backend.repository.BookRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookService {

    private final WebClient naverWebClient;
    private final WebClient aladinWebClient;
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final Validator validator;
    private final BookLogService bookLogService;

    public BookService(@Qualifier("naverWebClient") WebClient naverWebClient,
                       @Qualifier("aladinWebClient") WebClient aladinWebClient,
                       Validator validator,
                       BookRepository bookRepository,
                       CategoryService categoryService, BookLogService bookLogService) {
        this.naverWebClient = naverWebClient;
        this.aladinWebClient = aladinWebClient;
        this.validator = validator;
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.bookLogService = bookLogService;
    }


    private NaverBookSearchResponseDto callNaverBookSearchApi(String query, int start, int display) {
        return naverWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/book.json")
                        .queryParam("query", query)
                        .queryParam("start", start)
                        .queryParam("display", display)
                        .build())
                .retrieve()
                .bodyToMono(NaverBookSearchResponseDto.class)
                .block();
    }

    /**
     * 입력된 검색어로 네이버 책 검색 API를 통해 책을 검색한 결과 반환.
     * DB에 저장되어 있는 책과 isbn이 일치하는 책은 DB의 평점과 리뷰 수 정보가 병함되어 제공되며,
     * DB에 저장되어 있지 않은 책은 평점이 null, 리뷰 수가 0으로 나온다.
     * @param query 검색어
     * @param page 페이지 번호 (기본값 1)
     * @param size 페이지 내 결과 개수 (기본값 30)
     * @return 검색어로 검색된 책 정보 목록
     */
    @Transactional
    public BaseResponseDto<BookListDto<SearchBookInfoDto>>
    searchBooksByNaverApi(String query, int page, int size) {
        // 1. api 호출 결과 받기
        NaverBookSearchResponseDto apiResponse = callNaverBookSearchApi(query, (page - 1) * size + 1, size);

        // 2. DB에서 ISBN으로 도서 정보 조회
        List<String> isbnList = apiResponse.items().stream()
                .map(NaverBookItemDto::isbn).toList();

        List<Book> booksInDb = bookRepository.findAllByIsbnIn(isbnList);
        Map<String, Book> bookMap = booksInDb.stream()
                .collect(Collectors.toMap(Book::getIsbn, Function.identity()));

        // 3. DB에 있던 정보와 병합 (평점, 리뷰 수)
        List<SearchBookInfoDto> books = apiResponse.items().stream()
                .map(item -> {
                    Optional<Book> book = Optional.ofNullable(bookMap.get(item.isbn()));
                    return new SearchBookInfoDto(
                            item.isbn(),
                            item.image(),
                            item.title(),
                            item.author(),
                            book.map(Book::getId).orElse(null),
                            book.map(Book::getAverageRatingAsBigDecimal).orElse(null),
                            book.map(Book::getReviewCount).orElse(0)
                    );
                }).toList();

        // 4. meta 정보 생성해 함께 반환
        int totalPages = apiResponse.total() / size + (apiResponse.total() % size == 0 ? 0 : 1);
        // Page 객체의 pageNumber 기준으로 페이지네이션 정보 생성하는 기능이라 일반적으로 사용하는 페이지 번호에서 -1
        return BaseResponseDto.ofSuccessWithPagination(new BookListDto<>(books), "조회 성공",
                page - 1, totalPages, apiResponse.total());
    }

    private void validateAladinBookSearchResponseDto(AladinBookSearchResponseDto aladinResponseDto) {
        if (aladinResponseDto == null) {
            throw new OpenApiException(OpenApiExceptionInfo.API_CALL_ERROR);
        }
        Set<ConstraintViolation<AladinBookSearchResponseDto>> violations = validator.validate(aladinResponseDto);
        if (!violations.isEmpty()) {
            throw new OpenApiException(OpenApiExceptionInfo.ALADIN_BOOK_NOT_FOUND);
        }
    }

    private AladinBookItemDto callAladinIsbnBookSearchApi(String isbn) {
        // 1. 알라딘 상품 조회 api 호출
        AladinBookSearchResponseDto apiResponse = aladinWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ItemLookUp.aspx")
                        .queryParam("ttbkey", "{ttbkey}")
                        .queryParam("itemId", isbn)
                        .queryParam("itemIdType", "ISBN13")
                        .queryParam("cover", "Big")
                        .queryParam("output", "js")
                        .queryParam("version", "20131101")
                        .build())
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new OpenApiException(OpenApiExceptionInfo.API_CALL_ERROR))
                )
                .bodyToMono(AladinBookSearchResponseDto.class)
                .block();

        // 2. 응답 유효성 검사
        validateAladinBookSearchResponseDto(apiResponse);
        if (apiResponse.item().size() != 1) {
            throw new OpenApiException(OpenApiExceptionInfo.API_CALL_ERROR);
        }
        return apiResponse.item().getFirst();
    }

    private Book mapToBookEntity(AladinBookItemDto aladinBookItem) {
        // 카테고리 정보 파싱하여 DB에 저장된 Category 객체 찾기
        Category category = categoryService
                .findCategoryByCategoryName(aladinBookItem.categoryName())
                .orElse(null);

        return Book.builder()
                .title(aladinBookItem.title())
                .author(aladinBookItem.author())
                .coverImgUrl(aladinBookItem.cover())
                .publisher(aladinBookItem.publisher())
                .publishedDate(aladinBookItem.pubDate())
                .isbn(aladinBookItem.isbn13())
                .category(category)
                .build();
    }

    private Book fetchAndSaveBookByIsbn(String isbn) {
        Book book = mapToBookEntity(callAladinIsbnBookSearchApi(isbn));
        return bookRepository.save(book);
    }

    /**
     * ISBN으로 책을 찾아 책에 대한 상세 정보와 현재 유저의 책에 대한 상태 정보(읽은 책, 찜한 책, 리뷰 작성 여부) 반환
     * DB에서 먼저 찾아본 후, DB에 없으면 알라딘 상품 조회 API를 호출하여 책 정보를 가져와 DB에 저장하고 반환한다.
     * @param isbn 찾을 책의 ISBN(13자리)
     * @return 입력된 ISBN을 갖는 책에 대한 정보
     */
    @Transactional
    public BookAndUserStatusDto getBookDetailsByIsbn(String isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        // 1. DB에 책 정보 있을 경우 바로 응답 반환
        if (book.isPresent()) {
            return new BookAndUserStatusDto(
                    BookDetailsDto.fromEntity(book.get()),
                    bookLogService.getCurrentUserStatusAboutBook(book.get()).orElse(null)
            );
        }

        // 2. DB에 책 정보 없으면 알라딘 api 호출 -> 저장 -> 반환
        Book savedBook = fetchAndSaveBookByIsbn(isbn);
        return new BookAndUserStatusDto(
                BookDetailsDto.fromEntity(savedBook),
                bookLogService.getCurrentUserStatusAboutBook(savedBook).orElse(null)
        );
    }

    /**
     * bookId로 책을 찾아 책에 대한 상세 정보와 현재 유저의 책에 대한 상태 정보(읽은 책, 찜한 책, 리뷰 작성 여부) 반환
     * @param bookId 찾을 책의 ID(PK)
     * @return 입력된 bookId를 갖는 책에 대한 정보
     */
    @Transactional
    public BookAndUserStatusDto getBookDetailsById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));
        return new BookAndUserStatusDto(
                BookDetailsDto.fromEntity(book),
                bookLogService.getCurrentUserStatusAboutBook(book).orElse(null)
        );
    }

    /**
     * 알라딘 상품 목록 조회 API 호출하여 응답 Optional 반환
     * @param queryType (String) 상품 목록 종류 (Bestseller / ItemNewAll)
     * @param searchTarget (String) 조회 대상 Mall (Book(국내도서) / Foreign(외국도서))
     * @param start (Integer) 검색 결과 시작 페이지 (1 이상)
     * @param maxResults (Integer) 한 페이지당 최대 출력 개수 (1-50)
     */
    private Optional<AladinBookSearchResponseDto>
    callAladinItemListApi(String queryType, String searchTarget,
                          @Min(value = 1) Integer start,
                          @Min(value = 1) @Max(value = 50) Integer maxResults) {
        try {
            return Optional.ofNullable(aladinWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/ItemList.aspx")
                            .queryParam("ttbkey", "{ttbkey}")
                            .queryParam("queryType", queryType)         // 리스트 종류 (Bestseller / ItemNewAll)
                            .queryParam("searchTarget", searchTarget)   // 조회 대상 Mall (Book / Foreign)
                            .queryParam("start", start)                 // 기본값 1
                            .queryParam("maxResults", maxResults)       // 기본값 10
                            .queryParam("cover", "Big")          // 표지 크기
                            .queryParam("output", "js")
                            .queryParam("version", "20131101")
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            response -> Mono.error(new OpenApiException(OpenApiExceptionInfo.API_CALL_ERROR))
                    )
                    .bodyToMono(AladinBookSearchResponseDto.class)
                    .block());
        } catch (Exception e) {
            log.error("Aladin {} Mall {} ItemList API call failed.", searchTarget, queryType, e);
            return Optional.empty();
        }
    }

    /**
     * AladinBookItemDto에서 DB에 새로 저장해야 할 도서 골라 Book 목록으로 반환
     * @param aladinBookItems 알라딘 상품 리스트
     */
    private List<Book> filterNonExistingBooks(List<AladinBookItemDto> aladinBookItems) {
        // 입력 리스트에서 ISBN이 null인 것, 형식 안 맞는 것, 중복된 ISBN 제거
        Map<String, AladinBookItemDto> uniqueItems = aladinBookItems.stream()
                .filter(item -> item.isbn13() == null || !item.isbn13().matches(ValidationRegexConstants.ISBN_REGEX))
                .collect(Collectors.toMap(AladinBookItemDto::isbn13,
                        item -> item,
                        (existing, replacement) -> existing // 중복 시 첫 번째 데이터 유지
                ));

        List<String> existingIsbns = bookRepository.findAllByIsbnIn(uniqueItems.keySet().stream().toList())
                .stream().map(Book::getIsbn).toList();

        // DB에 이미 있는 것은 제거
        return uniqueItems.values().stream()
                .filter(item -> !existingIsbns.contains(item.isbn13()))
                .map(this::mapToBookEntity)
                .toList();
    }

    private void fetchAndSaveBooks(String queryType, String searchTarget, int totalCount) {
        int batchSize = 50;
        int totalPages = totalCount / batchSize;

        for (int start = 1; start <= totalPages; start++) {
            callAladinItemListApi(queryType, searchTarget, start, batchSize)
                    .ifPresent(apiResponse -> {
                        List<Book> filteredBooks = filterNonExistingBooks(apiResponse.item());
                        // TODO: 여러 개 한꺼번에 저장해도 에러 안 나는지 확인. 미리 DB에 있는 isbn, 리스트 안에서 겹치는 isbn은 제거해서 저장 성공해야 함.
//                        bookRepository.saveAll(filteredBooks);

                        for (Book book : filteredBooks) {
                            try {
                                bookRepository.save(book);
                            } catch (Exception e) {
                                System.out.println("failed to save book: " + e.getMessage());
                            }
                        }
                    });
        }
    }

    /**
     * 매주 월요일 오전 2시 자동 실행
     * 국내도서 베스트셀러 1000권, 해외도서 200권 받아 DB에 없는 것은 저장
     */
    @Scheduled(cron = "0 0 2 ? * MON", zone = "Asia/Seoul")
    @Transactional
    public void fetchAndSaveBestsellers() {
        log.info("Fetching bestsellers: Scheduled task executed at {}", LocalDateTime.now());
        try {
            fetchAndSaveBooks("Bestseller", "Book", 1000);
            fetchAndSaveBooks("Bestseller", "Foreign", 200);
            log.info("Successfully fetched bestsellers.");
        } catch (Exception e) {
            log.error("Failed to fetch bestsellers: {}", e.getMessage(), e);
        }
    }

    /**
     * 매주 화요일 오전 2시 자동 실행
     * 국내도서 신간 1000권 받아 DB에 없는 것은 저장
     */
    @Scheduled(cron = "0 0 2 ? * TUE", zone = "Asia/Seoul")
    @Transactional
    public void fetchAndSaveNewlyPublishedBooks() {
        log.info("Fetching newly published books: Scheduled task executed at {}", LocalDateTime.now());
        try {
            fetchAndSaveBooks("ItemNewAll", "Book", 1000);
            log.info("Successfully fetched newly published books.");
        } catch (Exception e) {
            log.error("Failed to fetch newly published books: {}", e.getMessage(), e);
        }
    }
}
