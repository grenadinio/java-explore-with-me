package ru.practicum.mainservice.exception;

public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
