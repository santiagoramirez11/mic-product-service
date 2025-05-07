package com.banreservas.product.service;

import com.banreservas.product.model.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(User user);
}
