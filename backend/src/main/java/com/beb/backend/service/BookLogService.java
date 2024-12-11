package com.beb.backend.service;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.ReadBook;
import com.beb.backend.dto.AddReadBookRequestDto;
import com.beb.backend.exception.BookException;
import com.beb.backend.exception.BookExceptionInfo;
import com.beb.backend.exception.ReadBookException;
import com.beb.backend.exception.ReadBookExceptionInfo;
import com.beb.backend.repository.BookRepository;
import com.beb.backend.repository.ReadBookRepository;
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

    @Transactional
    public void addBookToReadBook(AddReadBookRequestDto request) {
        Optional<Book> book = bookRepository.findById(request.bookId());
        if (book.isEmpty()) throw new BookException(BookExceptionInfo.BOOK_NOT_FOUND);

        Member member = memberService.getCurrentMember();
        if (readBookRepository.existsByMemberAndBook(member, book.get())) {
            throw new ReadBookException(ReadBookExceptionInfo.DUPLICATE_READ_BOOK);
        }

        ReadBook readBook = ReadBook.builder()
                .member(member)
                .book(book.get())
                .readAt(request.readAt()).build();
        readBookRepository.save(readBook);
    }
}
