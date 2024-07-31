package com.bangez.gateway.filter;

import com.bangez.gateway.domain.dto.LoginDTO;
import com.bangez.gateway.domain.model.PrincipalUserDetails;
import com.bangez.gateway.service.provider.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthFilter {
    private final WebClient webClient; //WebClient는 비동기 방식으로 HTTP 요청을 보낼 수 있는 클래스
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<ServerResponse> localLogin(LoginDTO dto) {
        return Mono.just(dto)
                .log()
                .flatMap(i ->
                        webClient.post()
                                .uri("lb://user-service/auth/login/local")
                                .accept(MediaType.APPLICATION_JSON)
                                .bodyValue(i)
                                .retrieve()
                                .bodyToMono(PrincipalUserDetails.class)
                )
                .log()
                .flatMap(i ->
                        jwtTokenProvider.generateToken(i, false)
                                .log()
                                .flatMap(accessToken ->
                                        jwtTokenProvider.generateToken(i, true)
                                                .log()
                                                .flatMap(refreshToken ->
                                                        ServerResponse.ok()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .cookie(
                                                                        ResponseCookie.from("accessToken")
                                                                                .value(accessToken)
                                                                                .maxAge(jwtTokenProvider.getAccessTokenExpired())
                                                                                .path("/")
                                                                                .sameSite("None") //sameSite는 크로스 도메인에 쿠키가 저장될 수 있도록 함.
                                                                                .secure(false)
                                                                                .httpOnly(true)
                                                                                .build()
                                                                )
                                                                .cookie(
                                                                        ResponseCookie.from("refreshToken")
                                                                                .value(refreshToken)
                                                                                .maxAge(jwtTokenProvider.getRefreshTokenExpired())
                                                                                .path("/")
                                                                                .sameSite("None")
                                                                                .secure(false)
                                                                                .httpOnly(true)
                                                                                .build()
                                                                )
                                                                .bodyValue(Boolean.TRUE)
                                                )
                                )
                );
    }

    public Mono<ServerResponse> refresh(String refreshToken) {
        return Mono.just(refreshToken)
                .flatMap(i -> Mono.just(jwtTokenProvider.removeBearer(refreshToken)))
                .filter(i -> jwtTokenProvider.isTokenValid(refreshToken, true))
                .filterWhen(i -> jwtTokenProvider.isTokenInRedis(refreshToken))
                .flatMap(i -> Mono.just(jwtTokenProvider.extractPrincipalUserDetails(refreshToken)))
                .flatMap(i -> jwtTokenProvider.generateToken(i, false))
                .flatMap(accessToken ->
                        ServerResponse.ok()
                                .cookie(
                                        ResponseCookie.from("accessToken")
                                                .value(accessToken)
                                                .maxAge(jwtTokenProvider.getAccessTokenExpired())
                                                .path("/")
                                                // .httpOnly(true)
                                                .build()
                                )
                                .build()
                );
    }

    public Mono<ServerResponse> logout(String refreshToken) {
        return Mono.just(refreshToken)
                .flatMap(i -> Mono.just(jwtTokenProvider.removeBearer(refreshToken)))
                .filter(i -> jwtTokenProvider.isTokenValid(refreshToken, true))
                .filterWhen(i -> jwtTokenProvider.isTokenInRedis(refreshToken))
                .filterWhen(i -> jwtTokenProvider.removeTokenInRedis(refreshToken))
                .flatMap(i -> ServerResponse.ok().build());
    }
}