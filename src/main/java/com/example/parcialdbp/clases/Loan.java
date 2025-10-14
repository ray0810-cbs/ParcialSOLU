package com.example.parcialdbp.clases;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="book_id")
    private Book book;

    @OneToOne
    @JoinColumn(name="user_id")
    private UserClass user;

    @Column
    private LocalDate borrowDate = LocalDate.now();

    @Column
    private LocalDate dueDate = borrowDate.plusDays(14);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


}
