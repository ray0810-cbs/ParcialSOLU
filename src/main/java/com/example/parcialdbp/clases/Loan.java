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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="book_id")
    private Book book;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserClass user;

    @Column
    private LocalDate borrowDate;

    @Column
    private LocalDate dueDate ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (borrowDate == null) borrowDate = LocalDate.now();
        if (dueDate == null) dueDate = borrowDate.plusDays(14);
        if (status == null) status = Status.ACTIVE;
    }


}
