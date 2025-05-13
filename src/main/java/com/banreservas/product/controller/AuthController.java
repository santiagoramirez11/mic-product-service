package com.banreservas.product.controller;

import com.banreservas.openapi.controllers.ServiceAuthApi;
import com.banreservas.openapi.models.AuthenticationRequestDto;
import com.banreservas.openapi.models.AuthenticationResponseDto;
import com.banreservas.openapi.models.UserRegistrationRequestDto;
import com.banreservas.openapi.models.UserResponseDto;
import com.banreservas.product.exception.InvalidLoginException;
import com.banreservas.product.mapper.UserAuthenticationRequestMapper;
import com.banreservas.product.model.User;
import com.banreservas.product.security.TokenInfo;
import com.banreservas.product.service.JwtProviderService;
import com.banreservas.product.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.banreservas.product.mapper.UserMapper.USER_MAPPER;

@Slf4j
@RestController
@RequestMapping("${app.api.base-url}")
@RequiredArgsConstructor
public class AuthController implements ServiceAuthApi {

    private final UserService userService;

    private final JwtProviderService jwtProviderService;

    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody User user) {
        return userService.createUser(user)
                .map(savedUser -> ResponseEntity.ok("User registered successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    private Mono<TokenInfo> createToken(AuthenticationRequestDto authRequest) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(), authRequest.getPassword()))
                .map(jwtProviderService::generateToken);
    }

    @Override
    public Mono<ResponseEntity<AuthenticationResponseDto>> login(AuthenticationRequestDto authenticationRequestDto, ServerWebExchange exchange) {
        return createToken(authenticationRequestDto)
                .switchIfEmpty(Mono.error(new InvalidLoginException("Invalid credentials")))
                .map(UserAuthenticationRequestMapper.AUTHENTICATION_MAPPER::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<UserResponseDto>> register(UserRegistrationRequestDto userRegistrationRequestDto, ServerWebExchange exchange) {
        return Mono.just(userRegistrationRequestDto)
                .map(USER_MAPPER::toUser)
                .doOnNext(user -> log.trace("Receive User [{}]", user))
                .flatMap(userService::createUser)
                .doOnSuccess(user -> log.info("Success creating user [id: {}]", user.getId()))
                .map(USER_MAPPER::toDto)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }
}
