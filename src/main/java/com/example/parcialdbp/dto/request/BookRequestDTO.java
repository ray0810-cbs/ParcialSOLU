package com.example.parcialdbp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDTO {
    @NotBlank(message="Ponle un título")
    private String title;

    @NotBlank(message="Ponle un autor")
    private String author;

    private String isbn;

    @Min(value = 0,message = "El numero de copias debe ser mayor a 0")
    private int totalCopies;
}
