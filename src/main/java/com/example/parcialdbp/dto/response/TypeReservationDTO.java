package com.example.parcialdbp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeReservationDTO {
    private String type;
    private Long id;
    private Long bookId;
    private String bookTitle;
    private ZonedDateTime reservedAt;
    private ZonedDateTime expiresAt;
    private String status;
}
