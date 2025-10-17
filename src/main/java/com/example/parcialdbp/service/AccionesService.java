package com.example.parcialdbp.service;

import com.example.parcialdbp.clases.*;
import com.example.parcialdbp.dto.request.BookRequestDTO;
import com.example.parcialdbp.dto.request.LoanRequestDTO;
import com.example.parcialdbp.dto.response.BookPageResponseDTO;
import com.example.parcialdbp.dto.response.BookResponseDTO;
import com.example.parcialdbp.dto.response.LoanResponseDTO;
import com.example.parcialdbp.repositorios.BookRepository;
import com.example.parcialdbp.repositorios.LoanRepository;
import com.example.parcialdbp.repositorios.UserRepository;
import com.example.parcialdbp.seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccionesService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

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

    @Transactional
    public LoanResponseDTO prestamo(String header, LoanRequestDTO loanRequestDTO){
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token no encontrado o inválido");
        }
        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnknownError("Usuario no encontrado"));

        Book book = bookRepository.findById(loanRequestDTO.getBookId())
                .orElseThrow(() -> new UnknownError("Libro no encontrado"));

        if (book.getAvailableCopies() <= 0)
            throw new UnknownError("No hay copias disponibles");

        boolean hasOverdue = loanRepository.existsByUserAndStatus(user, Status.OVERDUE);
        if (hasOverdue)
            throw new UnknownError("Tienes préstamos vencidos");

        LocalDate borrowDate = loanRequestDTO.getBorrowDate() != null ? loanRequestDTO.getBorrowDate() : LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);

        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(Status.ACTIVE)
                .build();

        int disponibles = book.getAvailableCopies();
        if (disponibles <= 0) {
            throw new UnknownError("No hay ejemplares disponibles");
        }
        book.setAvailableCopies(disponibles - 1);

        bookRepository.save(book);
        Loan saved = loanRepository.save(loan);

        LoanResponseDTO response = modelMapper.map(saved, LoanResponseDTO.class);
        response.setBorrowerName(user.getUsername());
        response.setUserId(user.getId());
        response.setBookId(book.getId());
        return response;


    }




}
