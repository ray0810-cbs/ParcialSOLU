package com.example.parcialdbp.controllers;

import com.example.parcialdbp.dto.request.BookRequestDTO;
import com.example.parcialdbp.dto.response.BookPageResponseDTO;
import com.example.parcialdbp.dto.response.BookResponseDTO;
import com.example.parcialdbp.dto.response.UserResponseDTO;
import com.example.parcialdbp.service.AccionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AccionesController {
    private final AccionesService accionesService;

    @PostMapping("/books")
    public ResponseEntity<BookResponseDTO> crear(@Valid @RequestBody BookRequestDTO requestDTO){
        BookResponseDTO bookResponseDTO = accionesService.crearLibro(requestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDTO);
    }

    @GetMapping("/books")
    public ResponseEntity<BookPageResponseDTO> listar(@Valid @RequestBody BookRequestDTO requestDTO){
        BookPageResponseDTO bookPageResponseDTO = accionesService.listarLibros();
        return  ResponseEntity.status(HttpStatus.CREATED).body(bookPageResponseDTO);
    }


}
