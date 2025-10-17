package com.example.parcialdbp.repositorios;

import com.example.parcialdbp.clases.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    boolean existsByIsbn(String isbn);

    List<Book> findByAvailableCopiesGreaterThan(int copies);
    List<Book> findByAvailableCopiesEquals(int value);
}
