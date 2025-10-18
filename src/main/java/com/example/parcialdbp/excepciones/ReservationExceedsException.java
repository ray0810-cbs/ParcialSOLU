package com.example.parcialdbp.excepciones;

public class ReservationExceedsException extends RuntimeException {
    public ReservationExceedsException(String message) {
        super(message);
    }
}
