package com.beb.backend.repository;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.ReadBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReadBookRepository extends CrudRepository<ReadBook, Long> {
    List<ReadBook> findByMember(Member member);
    Page<ReadBook> findByMember(Member member, Pageable pageable);
    List<ReadBook> findByBook(Book book);

    boolean existsByMemberAndBook(Member member, Book book);
}
