package com.beb.backend.service;

import com.beb.backend.domain.*;
import com.beb.backend.dto.requestDto.AddReadBookDto;
import com.beb.backend.dto.requestDto.AddWishlistBookDto;
import com.beb.backend.dto.requestDto.CreateReviewDto;
import com.beb.backend.dto.requestDto.UpdateReviewDto;
import com.beb.backend.dto.responseDto.*;
import com.beb.backend.exception.*;
import com.beb.backend.repository.*;
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
    private final ReviewLikeRepository reviewLikeRepository;


    private UserReadBookDto mapToUserReadBookDto(ReadBook readBook) {
        return new UserReadBookDto(
                readBook.getId(),
                BookSummaryDto.fromEntity(readBook.getBook()),
                readBook.getReadAt(),
                readBook.getCreatedAt()
        );
    }

    private BaseResponseDto<ReadBookListDto<UserReadBookDto>> getReadBooksForMember(Member member, Pageable pageable) {
        Page<ReadBook> readBookPage = readBookRepository.findByMember(member, pageable);
        List<UserReadBookDto> readBooks = readBookPage.getContent().stream()
                .map(this::mapToUserReadBookDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                readBookPage.getNumber(), readBookPage.getTotalPages(), readBookPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new ReadBookListDto<>(readBooks), meta);
    }

    @Transactional
    public BaseResponseDto<ReadBookListDto<UserReadBookDto>> getCurrentUserReadBooks(Pageable pageable) {
        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return getReadBooksForMember(member, pageable);
    }

    @Transactional
    public BaseResponseDto<ReadBookListDto<UserReadBookDto>> getUserReadBooks(Long memberId, Pageable pageable) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return getReadBooksForMember(member, pageable);
    }

    @Transactional
    public void addBookToReadBook(AddReadBookDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
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
        ReadBook readBook = readBookRepository.findById(readBookId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.READ_BOOK_NOT_FOUND));

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        if (!member.getId().equals(readBook.getMember().getId())) {
            throw new BookLogException(BookLogExceptionInfo.READ_BOOK_FORBIDDEN);
        }

        commentRepository.findByMemberAndBookAndParentCommentIsNull(member, readBook.getBook())
                .ifPresent(review -> deleteReview(review.getId()));
        readBookRepository.delete(readBook);
    }

    private UserWishlistBookDto mapToUserWishlistBookDto(WishlistBook wishlistBook) {
        return new UserWishlistBookDto(
                wishlistBook.getId(),
                BookSummaryDto.fromEntity(wishlistBook.getBook()),
                wishlistBook.getCreatedAt()
        );
    }

    private BaseResponseDto<WishlistBookListDto<UserWishlistBookDto>>
    getWishlistBooksForMember(Member member, Pageable pageable) {
        Page<WishlistBook> wishlistBookPage = wishlistBookRepository.findByMember(member, pageable);
        List<UserWishlistBookDto> wishlistBooks = wishlistBookPage.getContent().stream()
                .map(this::mapToUserWishlistBookDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                wishlistBookPage.getNumber(), wishlistBookPage.getTotalPages(), wishlistBookPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new WishlistBookListDto<>(wishlistBooks), meta);
    }

    @Transactional
    public BaseResponseDto<WishlistBookListDto<UserWishlistBookDto>>
    getCurrentUserWishlistBooks(Pageable pageable) {
        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return getWishlistBooksForMember(member, pageable);
    }

    @Transactional
    public BaseResponseDto<WishlistBookListDto<UserWishlistBookDto>>
    getUserWishlistBooks(Long memberId, Pageable pageable) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return getWishlistBooksForMember(member, pageable);
    }

    @Transactional
    public void addBookToWishlistBook(AddWishlistBookDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
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
        WishlistBook wishlistBook = wishlistBookRepository.findById(wishlistBookId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.WISHLIST_BOOK_NOT_FOUND));

        memberService.getCurrentMember()
                .filter(member -> member.getId().equals(wishlistBook.getMember().getId()))
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.WISHLIST_BOOK_FORBIDDEN));

        wishlistBookRepository.delete(wishlistBook);
    }

    private UserReviewDto mapToUserReviewDto(Comment review, boolean isCurrentUser) {
        return new UserReviewDto(
                review.getId(),
                BookSummaryDto.fromEntity(review.getBook()),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                isCurrentUser ? null : review.getIsSpoiler()
        );
    }

    private BaseResponseDto<ReviewListDto<UserReviewDto>>
    getReviewsByMember(Member member, Pageable pageable, boolean isCurrentUser) {
        Page<Comment> reviewsPage = isCurrentUser ? commentRepository.findReviewsByMember(member, pageable)
                : commentRepository.findPublicReviewsByMember(member, pageable);
        List<UserReviewDto> reviews = reviewsPage.getContent().stream()
                .map(review -> mapToUserReviewDto(review, isCurrentUser)).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                reviewsPage.getNumber(), reviewsPage.getTotalPages(), reviewsPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new ReviewListDto<>(reviews), meta);
    }

    @Transactional
    public BaseResponseDto<ReviewListDto<UserReviewDto>> getCurrentUserReviews(Pageable pageable) {
        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        return getReviewsByMember(member, pageable, true);
    }

    @Transactional
    public BaseResponseDto<ReviewListDto<UserReviewDto>> getUserReviews(Long memberId, Pageable pageable) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        boolean isCurrentUser = memberService.getCurrentMember()
                .map(currentMember -> currentMember.getId().equals(member.getId()))
                .orElse(false);
        return getReviewsByMember(member, pageable, isCurrentUser);
    }

    private BookReviewDto mapToBookReviewDto(Comment review) {
        return new BookReviewDto(
                review.getId(),
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

    public BaseResponseDto<ReviewListDto<BookReviewDto>>
    getBookReviews(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Page<Comment> reviewsPage = memberService.getCurrentMember()
                .map(member -> commentRepository.findVisibleReviewsByBookIdAndMemberId(bookId, member.getId(), pageable))
                .orElseGet(() -> commentRepository.findPublicReviewsByBookId(bookId, pageable));

        List<BookReviewDto> reviews = reviewsPage.getContent().stream()
                .map(this::mapToBookReviewDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                reviewsPage.getNumber(), reviewsPage.getTotalPages(), reviewsPage.getTotalElements(),
                "조회 성공");
        return BaseResponseDto.success(new ReviewListDto<>(reviews), meta);
    }

    private ReviewDetailsDto mapToReviewDetailsDto(Comment review) {
        return new ReviewDetailsDto(
                review.getId(),
                BookSummaryDto.fromEntity(review.getBook()),
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

    public BaseResponseDto<ReviewListDto<ReviewDetailsDto>>
    getAllReviews(Pageable pageable) {

        Page<Comment> reviewsPage = memberService.getCurrentMember()
                .map(member -> commentRepository.findAllVisibleReviewsByMemberId(member.getId(), pageable))
                .orElseGet(() -> commentRepository.findAllPublicReviews(pageable));

        List<ReviewDetailsDto> reviews = reviewsPage.getContent().stream()
                .map(this::mapToReviewDetailsDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                reviewsPage.getNumber(), reviewsPage.getTotalPages(), reviewsPage.getTotalElements(),
                "조회 성공"
        );
        return BaseResponseDto.success(new ReviewListDto<>(reviews), meta);
    }

    @Transactional
    public ReviewIdDto createReview(CreateReviewDto request) {
        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));

        Comment savedReview = commentRepository.save(Comment.createReview(
                book, member,
                request.content(),
                request.rating(),
                request.isSpoiler(),
                request.isPublic()
        ));
        book.addRating(savedReview.getRating());

        if (!readBookRepository.existsByMemberAndBook(member, book)) {
            readBookRepository.save(ReadBook.builder().book(book).member(member).build());
        }
        return new ReviewIdDto(savedReview.getId());
    }

    @Transactional
    public ReviewDetailsDto getReviewDetails(Long reviewId) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        if (!review.getIsPublic()) {
            memberService.getCurrentMember()
                    .filter(member -> member.getId().equals(review.getMember().getId()))
                    .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_PUBLIC));
        }

        return mapToReviewDetailsDto(review);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewDto request) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        memberService.getCurrentMember()
                .filter(member -> member.getId().equals(review.getMember().getId()))
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_FORBIDDEN));

        if (request.rating() != null) {
            review.getBook().updateRating(review.getRating(), request.rating());
            review.setRating(request.rating());
        }
        if (request.content() != null) review.setContent(request.content());
        if (request.isSpoiler() != null) review.setIsSpoiler(request.isSpoiler());
        if (request.isPublic() != null) review.setIsPublic(request.isPublic());
        review.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Comment review = commentRepository.findById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        memberService.getCurrentMember()
                .filter(member -> member.getId().equals(review.getMember().getId()))
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_FORBIDDEN));

        Book book = bookRepository.findById(review.getBook().getId())
                .orElseThrow(() -> new BookException(BookExceptionInfo.BOOK_NOT_FOUND));

        commentRepository.delete(review);
        book.removeRating(review.getRating());
    }

    @Transactional
    public void createReviewLike(Long reviewId) {
        Comment review = commentRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));

        if (reviewLikeRepository.existsByMemberIdAndCommentId(member.getId(), review.getId())) {
            throw new BookLogException(BookLogExceptionInfo.REVIEW_LIKE_ALREADY_EXISTS);
        }
        reviewLikeRepository.save(ReviewLike.builder().member(member).comment(review).build());
        review.incrementLikeCount();
    }

    @Transactional
    public void deleteReviewLike(Long reviewId) {
        Comment review = commentRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));

        ReviewLike reviewLike = reviewLikeRepository.findByMemberIdAndReviewId(member.getId(), reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_LIKE_NOT_FOUND));
        reviewLikeRepository.delete(reviewLike);
        review.decrementLikeCount();
    }

    /**
     * 현재 사용자가 입력된 책을 찜한 책으로 등록했는지, 읽은 책으로 등록했는지, 책에 대한 리뷰를 작성했는지에 대한 정보를 반환
     * 인증되지 않은 사용자는 빈 Optional로 반환
     * @param book (Book) 확인할 도서 객체
     */
    public Optional<UserStatusDto> getCurrentUserStatusAboutBook(Book book) {
        return memberService.getCurrentMember()
                .map(member -> new UserStatusDto(
                        wishlistBookRepository.existsByMemberAndBook(member, book),
                        readBookRepository.existsByMemberAndBook(member, book),
                        commentRepository.existsByMemberAndBookAndParentCommentIsNull(member, book)
                ));
    }
}
