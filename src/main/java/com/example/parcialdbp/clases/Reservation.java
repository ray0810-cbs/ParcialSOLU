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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private UserClass user;

    @Column
    private ZonedDateTime reservedAt;

    @Column
    private ZonedDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
        if (this.reservedAt == null) {
            this.reservedAt = ZonedDateTime.now();
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.reservedAt.plusHours(48);
        }
    }
}
