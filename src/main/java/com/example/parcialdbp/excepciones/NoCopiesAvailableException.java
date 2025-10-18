package com.example.parcialdbp.excepciones;

public class NoCopiesAvailableException extends RuntimeException {
    public NoCopiesAvailableException(String message) {
        super(message);
    }
}
