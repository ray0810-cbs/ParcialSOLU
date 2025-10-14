package com.example.parcialdbp.clases;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="reservations")
public class Reservation {
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
    private ZonedDateTime reservedAt;

    @Column
    private ZonedDateTime expiresAt =  reservedAt.plusHours(48);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}
