package com.banreservas.product.service;

import com.banreservas.product.security.TokenInfo;
import org.springframework.security.core.Authentication;

public interface JwtProviderService {

    TokenInfo generateToken(Authentication authentication);

    Authentication getAuthentication(String token);

    boolean validateToken(String token);

}
