package com.example.parcialdbp;

import com.example.parcialdbp.clases.Book;
import com.example.parcialdbp.repositorios.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final BookRepository bookRepository;

    @PostConstruct
    public void init() {
        if (bookRepository.count() == 0) {
            Book b1 = Book.builder()
                    .title("Cien años de soledad")
                    .author("Gabriel García Márquez")
                    .isbn("9780060883287")
                    .totalCopies(3)
                    .availableCopies(2)
                    .build();

            Book b2 = Book.builder()
                    .title("1984")
                    .author("George Orwell")
                    .isbn("9780451524935")
                    .totalCopies(2)
                    .availableCopies(1)
                    .build();

            bookRepository.save(b1);
            bookRepository.save(b2);
        }
    }
}