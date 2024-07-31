package com.bangez.gateway.service;

import com.bangez.gateway.domain.dto.LoginDTO;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<ServerResponse> localLogin(LoginDTO dto);
    Mono<ServerResponse> refresh(String refreshToken);
    Mono<ServerResponse> logout(String refreshToken);
}