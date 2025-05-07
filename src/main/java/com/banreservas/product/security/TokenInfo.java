package com.banreservas.product.security;

public record TokenInfo(String accessToken, long expiresIn, String tokenType) {
}
