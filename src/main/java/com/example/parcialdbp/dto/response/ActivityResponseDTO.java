package com.example.parcialdbp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDTO {
    private List<Object> content;
    private int page;
    private int size;
    private int totalElements;
}
