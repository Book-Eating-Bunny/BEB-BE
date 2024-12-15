package com.beb.backend.service;

import com.beb.backend.domain.*;
import com.beb.backend.dto.*;
import com.beb.backend.exception.*;
import com.beb.backend.repository.BookRepository;
import com.beb.backend.repository.CommentRepository;
import com.beb.backend.repository.ReadBookRepository;
import com.beb.backend.repository.WishlistBookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookLogService {
    private final MemberService memberService;
    private final BookRepository bookRepository;
    private final ReadBookRepository readBookRepository;
    private final WishlistBookRepository wishlistBookRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addBookToReadBook(AddReadBookRequestDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember();
        if (readBookRepository.existsByMemberAndBook(member, book.get())) {
            throw new BookLogException(BookLogExceptionInfo.DUPLICATE_READ_BOOK);
        }

        ReadBook readBook = ReadBook.builder()
                .member(member)
                .book(book.get())
                .readAt(request.readAt()).build();
        readBookRepository.save(readBook);
    }

    @Transactional
    public void deleteBookFromReadBook(Long readBookId) {
        if (!readBookRepository.existsById(readBookId)) {
            throw new BookLogException(BookLogExceptionInfo.READ_BOOK_NOT_FOUND);
        }
        readBookRepository.deleteById(readBookId);
    }

    @Transactional
    public void addBookToWishlistBook(AddWishlistBookRequestDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember();
        if (wishlistBookRepository.existsByMemberAndBook(member, book.get())) {
            throw new BookLogException(BookLogExceptionInfo.DUPLICATE_WISHLIST_BOOK);
        }

        WishlistBook wishlistBook = WishlistBook.builder()
                .member(member)
                .book(book.get()).build();
        wishlistBookRepository.save(wishlistBook);
    }

    @Transactional
    public void deleteBookFromWishlistBook(Long wishlistBookId) {
        if (!wishlistBookRepository.existsById(wishlistBookId)) {
            throw new BookLogException(BookLogExceptionInfo.WISHLIST_BOOK_NOT_FOUND);
        }
        wishlistBookRepository.deleteById(wishlistBookId);
    }

    @Transactional
    public BaseResponseDto<ReviewsResponseDto<CurrentUserReviewDto>> getUserReviewsById(Long memberId, Pageable pageable) {
        Page<Comment> reviewsPage = commentRepository.findReviewsByMemberId(memberId, pageable);

        List<CurrentUserReviewDto> reviews = reviewsPage.getContent().stream()
                .map(review -> new CurrentUserReviewDto(
                        review.getId(),
                        new BookSummaryDto(
                                review.getBook().getId(),
                                review.getBook().getCoverImgUrl(),
                                review.getBook().getTitle(),
                                review.getBook().getAuthor()
                        ),
                        review.getRating(),
                        review.getContent(),
                        review.getCreatedAt(),
                        review.getUpdatedAt()
                )).toList();

        BaseResponseDto.Meta meta = new BaseResponseDto.Meta(
                "조회 성공",
                reviewsPage.getNumber(),
                reviewsPage.getTotalPages(),
                reviewsPage.getTotalElements());

        return BaseResponseDto.success(new ReviewsResponseDto<CurrentUserReviewDto>(reviews), meta);
    }

    @Transactional
    public CreateReviewResponseDto createReview(CreateReviewRequestDto request) {
        Member member = memberService.getCurrentMember();
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));

        Comment savedReview = commentRepository.save(Comment.createReview(
                book, member,
                request.content(),
                request.rating(),
                request.isSpoiler(),
                request.isPublic()
        ));
        book.incrementReviewCount();

        if (!readBookRepository.existsByMemberAndBook(member, book)) {
            readBookRepository.save(ReadBook.builder().book(book).member(member).build());
        }
        return new CreateReviewResponseDto(savedReview.getId());
    }

    @Transactional
    public ReviewDetailsResponseDto getReviewDetails(Long reviewId) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        if (!review.getIsPublic()) {
            try {
                Member member = memberService.getCurrentMember();
                if (!member.getId().equals(review.getMember().getId())) {
                    throw new BookLogException(BookLogExceptionInfo.FORBIDDEN_REVIEW);
                }
            } catch (MemberException e) {
                throw new BookLogException(BookLogExceptionInfo.FORBIDDEN_REVIEW);
            }
        }

        return new ReviewDetailsResponseDto(
                review.getId(),
                new BookSummaryDto(
                        review.getBook().getId(),
                        review.getBook().getCoverImgUrl(),
                        review.getBook().getTitle(),
                        review.getBook().getAuthor()
                ),
                new MemberSummaryDto(
                        review.getMember().getId(),
                        review.getMember().getNickname()
                ),
                review.getRating(),
                review.getContent(),
                review.getIsSpoiler(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
