package com.shiryu.url_shortener_kafka.exceptions;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String message) {
        super(message);
    }
}
