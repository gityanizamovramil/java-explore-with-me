package ru.practicum.ewm.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
