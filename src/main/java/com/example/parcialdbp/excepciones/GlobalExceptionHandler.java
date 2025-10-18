package com.example.parcialdbp.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUserNotFoundException(UserNotFoundException ex){
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado",ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Usuario ya existe", ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFoundException(BookNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Libro no encontrado", ex.getMessage());
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBookAlreadyExists(BookAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Ese libro ya existe", ex.getMessage());
    }

    @ExceptionHandler(NoCopiesAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleNoCopiesAvailableException(NoCopiesAvailableException ex){
        return buildErrorResponse(HttpStatus.CONFLICT,"No hay copias disponibles",ex.getMessage());
    }

    @ExceptionHandler(CopiesAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleCopiesAvailableException(CopiesAvailableException ex){
        return buildErrorResponse(HttpStatus.CONFLICT,"Hay copias disponibles",ex.getMessage());
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleLoanNotFoundException(LoanNotFoundException ex){
        return buildErrorResponse(HttpStatus.NOT_FOUND, "No procede préstamo, id inexistente",ex.getMessage());
    }

    @ExceptionHandler(OverdueLoanException.class)
    public ResponseEntity<Map<String, Object>> handleOverdueLoanException(OverdueLoanException ex){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Hay préstamos vencidos", ex.getMessage());
    }

    @ExceptionHandler(ReservationExceedsException.class)
    public ResponseEntity<Map<String, Object>> handleReservationExceedsException(ReservationExceedsException ex){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Hay más de tres reservas", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Error de validación");
        response.put("message", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

}
