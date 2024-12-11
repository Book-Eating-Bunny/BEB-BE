package com.beb.backend.service;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.ReadBook;
import com.beb.backend.domain.WishlistBook;
import com.beb.backend.dto.AddReadBookRequestDto;
import com.beb.backend.dto.AddWishlistBookRequestDto;
import com.beb.backend.exception.BookException;
import com.beb.backend.exception.BookExceptionInfo;
import com.beb.backend.exception.BookLogException;
import com.beb.backend.exception.BookLogExceptionInfo;
import com.beb.backend.repository.BookRepository;
import com.beb.backend.repository.ReadBookRepository;
import com.beb.backend.repository.WishlistBookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookLogService {
    private final MemberService memberService;
    private final BookRepository bookRepository;
    private final ReadBookRepository readBookRepository;
    private final WishlistBookRepository wishlistBookRepository;

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
}
