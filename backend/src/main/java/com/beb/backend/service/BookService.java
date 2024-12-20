package com.beb.backend.service;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Category;
import com.beb.backend.dto.*;
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

    @Transactional
    public BaseResponseDto<BooksResponseDto<SearchBookInfoDto>>
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
        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                page - 1, totalPages, apiResponse.total(), "조회 성공"
        );  // Page 객체의 pageNumber 기준으로 페이지네이션 정보 생성하는 기능이라 일반적으로 사용하는 페이지 번호에서 -1
        return BaseResponseDto.success(new BooksResponseDto<>(books), meta);
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

    @Transactional
    public BookAndUserStatusDto getBookDetailsById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));
        return new BookAndUserStatusDto(
                BookDetailsDto.fromEntity(book),
                bookLogService.getCurrentUserStatusAboutBook(book).orElse(null)
        );
    }
}
