package com.bangez.user.domain.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Component
@ToString
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private String passwordConfirm;
    private String name;
    private Long phone;
    private String email;
}
