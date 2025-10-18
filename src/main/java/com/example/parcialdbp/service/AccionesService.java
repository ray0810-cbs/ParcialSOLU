package com.example.parcialdbp.service;

import com.example.parcialdbp.clases.*;
import com.example.parcialdbp.dto.request.BookRequestDTO;
import com.example.parcialdbp.dto.request.LoanRequestDTO;
import com.example.parcialdbp.dto.request.ReservaRequestDTO;
import com.example.parcialdbp.dto.response.*;
import com.example.parcialdbp.repositorios.BookRepository;
import com.example.parcialdbp.repositorios.LoanRepository;
import com.example.parcialdbp.repositorios.ReservationRepository;
import com.example.parcialdbp.repositorios.UserRepository;
import com.example.parcialdbp.seguridad.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccionesService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
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
    public BookPageResponseDTO listarLibros(String status,int page, int size) {

        List<Book> books = switch (status.toLowerCase()) {
            case "available" -> bookRepository.findByAvailableCopiesGreaterThan(0);
            case "unavailable" -> bookRepository.findByAvailableCopiesEquals(0);
            default -> bookRepository.findAll();
        };

        int totalElements = books.size();
        int fromIndex = page * size; //Indice inicial
        int toIndex = Math.min(fromIndex + size, totalElements); //Indice final

        List<Book> pageContent;
        if (fromIndex >= totalElements) {
            pageContent = List.of(); // lista vacía si no hay resultados
        } else {
            pageContent = books.subList(fromIndex, toIndex);
        }

        List<BookResponseDTO> bookResponseDTOs = pageContent.stream()
                .map(book -> modelMapper.map(book, BookResponseDTO.class))
                .toList();

        // Calcular la suma total de todas las copias
        int totalCopiesSum = bookResponseDTOs.stream()
                .mapToInt(BookResponseDTO::getTotalCopies) // toma cada totalCopies
                .sum();

        BookPageResponseDTO bookPageResponseDTO = new BookPageResponseDTO();
        bookPageResponseDTO.setContent(bookResponseDTOs);
        bookPageResponseDTO.setPage(page);
        bookPageResponseDTO.setSize(size);
        bookPageResponseDTO.setTotalElements(totalCopiesSum);

        return bookPageResponseDTO;
    }

    @Transactional
    public LoanResponseDTO crearPrestamo(String header, LoanRequestDTO loanRequestDTO){
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnknownError("Token no encontrado o inválido");
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

        Loan saved = loanRepository.save(loan);

        LoanResponseDTO response = modelMapper.map(saved, LoanResponseDTO.class);
        response.setBorrowerName(user.getUsername());
        response.setUserId(user.getId());
        response.setBookId(book.getId());
        return response;
    }

    @Transactional
    public ReservaResponseDTO crearReserva(String header, ReservaRequestDTO reservaRequestDTO){
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnknownError("Token no encontrado o inválido");
        }
        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnknownError("Usuario no encontrado"));

        Book book = bookRepository.findById(reservaRequestDTO.getBookId())
                .orElseThrow(() -> new UnknownError("Libro no encontrado"));

        if (book.getAvailableCopies() > 0) {
            throw new UnknownError("Hay ejemplares disponibles, no hay sentido en hacer una reserva");
        }

        long reservasActivas = reservationRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(user.getId()))
                .filter(r -> r.getStatus() == Status.PENDING)
                .count();

        if (reservasActivas >= 3) {
            throw new RuntimeException("No puedes tener más de 3 reservas activas");
        }

        Reservation reservation = Reservation.builder()
                .book(book)
                .user(user)
                .status(Status.PENDING)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        ReservaResponseDTO response = modelMapper.map(saved, ReservaResponseDTO.class);

        response.setBookId(book.getId());
        response.setBookTitle(book.getTitle());
        response.setUserId(user.getId());
        response.setUserName(user.getUsername());
        return response;
    }

    @Transactional(readOnly = true)
    public ActivityResponseDTO miActividad(String header, String type, int page, int size) {

        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnknownError("Token no encontrado o inválido");
        }
        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        UserClass user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        List<Object> actividades = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "loans" -> {
                List<Loan> loans = loanRepository.findAll().stream()
                        .filter(l -> l.getUser() != null && l.getUser().getId().equals(user.getId()))
                        .filter(l -> l.getStatus() == Status.ACTIVE ||
                                l.getStatus() == Status.RETURNED ||
                                l.getStatus() == Status.OVERDUE)
                        .toList();

                loans.forEach(l -> {
                    TypeLoanDTO dto = modelMapper.map(l, TypeLoanDTO.class);
                    dto.setBookId(l.getBook().getId());
                    dto.setBookTitle(l.getBook().getTitle());
                    dto.setType("LOAN");
                    actividades.add(dto);
                });
            }
            case "reservations" -> {
                List<Reservation> reservas = reservationRepository.findAll().stream()
                        .filter(r -> r.getUser() != null && r.getUser().getId().equals(user.getId()))
                        .filter(r -> r.getStatus() == Status.PENDING ||
                                r.getStatus() == Status.EXPIRED ||
                                r.getStatus() == Status.CANCELLED)
                        .toList();

                reservas.forEach(r -> {
                    TypeReservationDTO dto = modelMapper.map(r, TypeReservationDTO.class);
                    dto.setBookId(r.getBook().getId());
                    dto.setBookTitle(r.getBook().getTitle());
                    dto.setType("RESERVATION");
                    actividades.add(dto);
                });
            }
            default -> {
                // Mezclar préstamos y reservas
                List<Loan> loans = loanRepository.findAll().stream()
                        .filter(l -> l.getUser() != null && l.getUser().getId().equals(user.getId()))
                        .toList();
                loans.forEach(l -> {
                    TypeLoanDTO dto = modelMapper.map(l, TypeLoanDTO.class);
                    dto.setBookId(l.getBook().getId());
                    dto.setBookTitle(l.getBook().getTitle());
                    dto.setType("LOAN");
                    actividades.add(dto);
                });

                List<Reservation> reservas = reservationRepository.findAll().stream()
                        .filter(r -> r.getUser() != null && r.getUser().getId().equals(user.getId()))
                        .toList();
                reservas.forEach(r -> {
                    TypeReservationDTO dto = modelMapper.map(r, TypeReservationDTO.class);
                    dto.setBookId(r.getBook().getId());
                    dto.setBookTitle(r.getBook().getTitle());
                    dto.setType("RESERVATION");
                    actividades.add(dto);
                });
            }
        }

        actividades.sort((a, b) -> {
            ZonedDateTime fechaA = (a instanceof LoanResponseDTO l)
                    ? l.getBorrowDate().atStartOfDay(ZoneId.systemDefault())
                    : ((ReservaResponseDTO) a).getReservedAt();
            ZonedDateTime fechaB = (b instanceof LoanResponseDTO l)
                    ? l.getBorrowDate().atStartOfDay(ZoneId.systemDefault())
                    : ((ReservaResponseDTO) b).getReservedAt();
            return fechaB.compareTo(fechaA); // orden descendente
        });

        int totalElements = actividades.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<Object> pageContent;
        if (fromIndex >= totalElements) {
            pageContent = List.of();
        } else {
            pageContent = actividades.subList(fromIndex, toIndex);
        }

        ActivityResponseDTO response = new ActivityResponseDTO();
        response.setContent(pageContent);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);

        return response;
    }




}
