package com.beb.backend.repository;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.ReadBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReadBookRepository extends CrudRepository<ReadBook, Long> {
    List<ReadBook> findByMember(Member member);
    List<ReadBook> findByBook(Book book);

    boolean existsByMemberAndBook(Member member, Book book);
}
