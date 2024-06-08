package com.placeholder.bobanco.exception;

public class ClienteException extends RuntimeException{

    public ClienteException(String message) {
        super(message);
    }

    public static class DuplicateClienteException extends ClienteException {
        public DuplicateClienteException() {
            super("Cliente já cadastrado");
        }
    }
    
}
