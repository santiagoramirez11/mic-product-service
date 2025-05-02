package com.banreservas.product.config.properties;

public record Token(String secret, long expirationTime) {
}
