package com.example.parcialdbp.controllers;

import com.example.parcialdbp.dto.request.BookRequestDTO;
import com.example.parcialdbp.dto.request.LoanRequestDTO;
import com.example.parcialdbp.dto.request.ReservaRequestDTO;
import com.example.parcialdbp.dto.response.*;
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
    public ResponseEntity<BookPageResponseDTO> listar(@RequestParam(defaultValue = "all") String status,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        BookPageResponseDTO bookPageResponseDTO = accionesService.listarLibros(status, page, size);
        return  ResponseEntity.status(HttpStatus.OK).body(bookPageResponseDTO);
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanResponseDTO> prestamo(@RequestHeader("Authorization") String authHeader,
                                                    @Valid @RequestBody LoanRequestDTO requestDTO){
        LoanResponseDTO loanResponseDTO = accionesService.crearPrestamo(authHeader,requestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(loanResponseDTO);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservaResponseDTO> reserva(@RequestHeader("Authorization") String authHeader,
                                                       @Valid @RequestBody ReservaRequestDTO requestDTO){
        ReservaResponseDTO reservaResponseDTO = accionesService.crearReserva(authHeader,requestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(reservaResponseDTO);
    }

    @GetMapping("/my-activity")
    public ResponseEntity<ActivityResponseDTO> reserva(@RequestHeader("Authorization") String authHeader,
                                                       @RequestParam(defaultValue = "all") String type,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        ActivityResponseDTO activityResponseDTO = accionesService.miActividad(authHeader,type,page,size);
        return  ResponseEntity.status(HttpStatus.OK).body(activityResponseDTO);
    }




}
