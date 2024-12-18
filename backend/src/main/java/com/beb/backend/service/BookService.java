package com.beb.backend.service;

import com.beb.backend.domain.Book;
import com.beb.backend.dto.*;
import com.beb.backend.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final WebClient naverWebClient;
    private final WebClient aladinWebClient;
    private final BookRepository bookRepository;

    public BookService(@Qualifier("naverWebClient") WebClient naverWebClient,
                       @Qualifier("aladinWebClient") WebClient aladinWebClient,
                       BookRepository bookRepository) {
        this.naverWebClient = naverWebClient;
        this.aladinWebClient = aladinWebClient;
        this.bookRepository = bookRepository;
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
}
