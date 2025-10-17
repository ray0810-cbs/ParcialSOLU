package com.example.parcialdbp.dto.request;

import ch.qos.logback.core.boolex.EvaluationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestDTO {
    @NotNull(message = "El campo bookId es obligatorio")
    @Min(value = 1, message = "El bookId debe ser mayor o igual a 1")
    private Long bookId;

    @NotNull(message = "Ingresar fecha es obligatorio")
    private LocalDate borrowDate;
}
