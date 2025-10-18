package com.example.parcialdbp.excepciones;

public class OverdueLoanException extends RuntimeException {
    public OverdueLoanException(String message) {
        super(message);
    }
}
