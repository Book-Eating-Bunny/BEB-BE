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

import java.time.LocalDateTime;
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


    private CurrentUserReadBookDto mapToCurrentUserReadBookDto(ReadBook readBook) {
        return new CurrentUserReadBookDto(
                readBook.getId(),
                new BookSummaryDto(
                        readBook.getBook().getId(),
                        readBook.getBook().getCoverImgUrl(),
                        readBook.getBook().getTitle(),
                        readBook.getBook().getAuthor()
                ),
                readBook.getReadAt(),
                readBook.getCreatedAt()
        );
    }

    @Transactional
    public BaseResponseDto<ReadBooksResponseDto<CurrentUserReadBookDto>> getUserReadBooksById(Member memberId, Pageable pageable) {
        Page<ReadBook> readBookPage = readBookRepository.findByMember(memberId, pageable);
        List<CurrentUserReadBookDto> readBooks = readBookPage.getContent().stream()
                .map(this::mapToCurrentUserReadBookDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                readBookPage.getNumber(), readBookPage.getTotalPages(), readBookPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new ReadBooksResponseDto<>(readBooks), meta);
    }

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

        wishlistBookRepository.findByMemberAndBook(member, book.get())
                .ifPresent(wishlistBookRepository::delete);
    }

    @Transactional
    public void deleteBookFromReadBook(Long readBookId) {
        if (!readBookRepository.existsById(readBookId)) {
            throw new BookLogException(BookLogExceptionInfo.READ_BOOK_NOT_FOUND);
        }
        readBookRepository.deleteById(readBookId);
    }

    private CurrentUserWishlistBookDto mapToCurrentUserWishlistBookDto(WishlistBook wishlistBook) {
        return new CurrentUserWishlistBookDto(
                wishlistBook.getId(),
                new BookSummaryDto(
                        wishlistBook.getBook().getId(),
                        wishlistBook.getBook().getCoverImgUrl(),
                        wishlistBook.getBook().getTitle(),
                        wishlistBook.getBook().getAuthor()
                ),
                wishlistBook.getCreatedAt()
        );
    }

    @Transactional
    public BaseResponseDto<WishlistBooksResponseDto<CurrentUserWishlistBookDto>>
    getUserWishlistBooksById(Member memberId, Pageable pageable) {

        Page<WishlistBook> wishlistBookPage = wishlistBookRepository.findByMember(memberId, pageable);
        List<CurrentUserWishlistBookDto> wishlistBooks = wishlistBookPage.getContent().stream()
                .map(this::mapToCurrentUserWishlistBookDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                wishlistBookPage.getNumber(), wishlistBookPage.getTotalPages(), wishlistBookPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new WishlistBooksResponseDto<>(wishlistBooks), meta);
    }

    @Transactional
    public void addBookToWishlistBook(AddWishlistBookRequestDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember();
        if (readBookRepository.existsByMemberAndBook(member, book.get())) {
            throw new BookLogException(BookLogExceptionInfo.CANNOT_ADD_READ_BOOK_TO_WISHLIST_BOOK);
        }
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

    private CurrentUserReviewDto mapToCurrentUserReviewDto(Comment review) {
        return new CurrentUserReviewDto(
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
        );
    }

    @Transactional
    public BaseResponseDto<ReviewsResponseDto<CurrentUserReviewDto>> getUserReviewsById(Long memberId, Pageable pageable) {
        Page<Comment> reviewsPage = commentRepository.findReviewsByMemberId(memberId, pageable);
        List<CurrentUserReviewDto> reviews = reviewsPage.getContent().stream()
                .map(this::mapToCurrentUserReviewDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                reviewsPage.getNumber(), reviewsPage.getTotalPages(), reviewsPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new ReviewsResponseDto<>(reviews), meta);
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
                    throw new BookLogException(BookLogExceptionInfo.REVIEW_NOT_PUBLIC);
                }
            } catch (MemberException e) {
                throw new BookLogException(BookLogExceptionInfo.REVIEW_NOT_PUBLIC);
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

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequestDto request) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));
        Member member = memberService.getCurrentMember();
        if (!member.getId().equals(review.getMember().getId())) {
            throw new BookLogException(BookLogExceptionInfo.REVIEW_FORBIDDEN);
        }
        if (request.rating() != null) review.setRating(request.rating());
        if (request.content() != null) review.setContent(request.content());
        if (request.isSpoiler() != null) review.setIsSpoiler(request.isSpoiler());
        if (request.isPublic() != null) review.setIsPublic(request.isPublic());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));
        Member member = memberService.getCurrentMember();
        if (!member.getId().equals(review.getMember().getId())) {
            throw new BookLogException(BookLogExceptionInfo.REVIEW_FORBIDDEN);
        }
        Book book = bookRepository.findById(review.getBook().getId())
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));

        commentRepository.delete(review);
        book.decrementReviewCount();
    }

    /**
     * 현재 사용자가 입력된 책을 찜한 책으로 등록했는지, 읽은 책으로 등록했는지, 책에 대한 리뷰를 작성했는지에 대한 정보를 반환
     * 인증되지 않은 사용자는 빈 Optional로 반환
     * @param book (Book) 확인할 도서 객체
     */
    public Optional<UserStatusDto> getCurrentUserStatusAboutBook(Book book) {
        try {
            Member member = memberService.getCurrentMember();
            return Optional.of(new UserStatusDto(
                    wishlistBookRepository.existsByMemberAndBook(member, book),
                    readBookRepository.existsByMemberAndBook(member, book),
                    commentRepository.existsByMemberAndBookAndParentCommentIsNull(member, book)
            ));
        } catch (MemberException e) {
            return Optional.empty();
        }
    }
}
