package com.bangez.gateway.router;

import com.bangez.gateway.domain.dto.LoginDTO;
import com.bangez.gateway.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRouter {
//        private final AuthService authService;
    private final AuthFilter authFilter;

    //    @PostMapping("/login/local")
//    public Mono<ServerResponse> login(@RequestBody LoginDTO dto) {
//        return authService.localLogin(dto);
//    }
//
//    @PostMapping("/refresh")
//    public Mono<ServerResponse> refresh(@RequestHeader(name = "Authorization") String refreshToken) {
//        return authService.refresh(refreshToken);
//    }
//
//    @PostMapping("/logout")
//    public Mono<ServerResponse> logout(@RequestHeader(name = "Authorization") String refreshToken) {
//        return authService.logout(refreshToken);
//    }
    @Bean
    RouterFunction<ServerResponse> authRoutes() {
        return RouterFunctions.route()
                .POST("/auth/login/local", req -> req.bodyToMono(LoginDTO.class).flatMap(authFilter::localLogin))
                .POST("/auth/refresh", req -> ServerResponse.ok().build())
                .POST("/auth/logout", req -> ServerResponse.ok().build())
                .build();
    }
}