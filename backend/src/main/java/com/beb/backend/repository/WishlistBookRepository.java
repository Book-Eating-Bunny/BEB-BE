package com.beb.backend.repository;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Member;
import com.beb.backend.domain.WishlistBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WishlistBookRepository extends CrudRepository<WishlistBook, Long> {
    List<WishlistBook> findByMember(Member member);
    Page<WishlistBook> findByMember(Member member, Pageable pageable);
    List<WishlistBook> findByBook(Book book);

    boolean existsByMemberAndBook(Member member, Book book);
}
