package ru.yandex.schedule.httpserver.exception;

public class BadRequest extends Exception {
    public BadRequest(String message) {
        super(message);
    }
}
