package com.example.parcialdbp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private Long id;
    private Long bookId;
    private Long userId;
    private String borrowerName;
    private LocalDate borrowerDate;
    private LocalDate dueDate;
    private String status;
}
