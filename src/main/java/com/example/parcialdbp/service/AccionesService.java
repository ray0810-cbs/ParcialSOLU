package com.example.parcialdbp.service;

import com.example.parcialdbp.clases.Book;
import com.example.parcialdbp.clases.Rol;
import com.example.parcialdbp.clases.UserClass;
import com.example.parcialdbp.dto.request.BookRequestDTO;
import com.example.parcialdbp.dto.response.BookPageResponseDTO;
import com.example.parcialdbp.dto.response.BookResponseDTO;
import com.example.parcialdbp.repositorios.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccionesService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public BookResponseDTO crearLibro(BookRequestDTO bookRequestDTO) {
        if (bookRepository.existsByIsbn(bookRequestDTO.getIsbn())) {
            throw new UnknownError("Ese libro ya está registrado");
        }

        Book book = Book.builder()
                .title(bookRequestDTO.getTitle())
                .author(bookRequestDTO.getAuthor())
                .isbn(bookRequestDTO.getIsbn())
                .totalCopies(bookRequestDTO.getTotalCopies())
                .availableCopies(bookRequestDTO.getTotalCopies())
                .build();

        Book saved = bookRepository.save(book);

        BookResponseDTO bookResponseDTO = modelMapper.map(saved, BookResponseDTO.class);
        return bookResponseDTO;
    }

    @Transactional(readOnly = true)
    public BookPageResponseDTO listarLibros(String status) {
        bookRepository.findAll();
        List<Book> books = switch (status.toLowerCase()) {
            case "available" -> bookRepository.findByAvailableCopiesGreaterThan(0);
            case "unavailable" -> bookRepository.findByAvailableCopiesEquals(0);
            default -> bookRepository.findAll();
        };

        List<BookResponseDTO> bookResponseDTOs = books.stream()
                .map(book -> modelMapper.map(book, BookResponseDTO.class))
                .toList();

        // Calcular la suma total de todas las copias
        int totalCopiesSum = bookResponseDTOs.stream()
                .mapToInt(BookResponseDTO::getTotalCopies) // toma cada totalCopies
                .sum();

        BookPageResponseDTO bookPageResponseDTO = new BookPageResponseDTO();
        bookPageResponseDTO.setContent(bookResponseDTOs);
        bookPageResponseDTO.setTotalElements(totalCopiesSum);

        return bookPageResponseDTO;
    }




}
