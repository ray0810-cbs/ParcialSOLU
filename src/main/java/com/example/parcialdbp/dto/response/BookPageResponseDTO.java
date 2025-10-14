package com.example.parcialdbp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPageResponseDTO {
    private List<BookResponseDTO> content;
    private int page = 0;
    private int size = 10;
    private int totalElements;
}
