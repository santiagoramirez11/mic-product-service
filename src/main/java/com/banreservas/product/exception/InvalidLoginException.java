package com.banreservas.product.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidLoginException extends BadCredentialsException {

    public InvalidLoginException(String msg) {
        super(msg);
    }
}
