package com.bangez.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bangez.user.domain.dto.LoginDto;
import com.bangez.user.domain.dto.PrincipalUserDetails;
import com.bangez.user.domain.dto.UserModel;
import com.bangez.user.domain.model.User;
import com.bangez.user.domain.vo.ExceptionStatus;
import com.bangez.user.domain.vo.Role;
import com.bangez.user.exception.LoginException;
import com.bangez.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository repository;

    public PrincipalUserDetails login(LoginDto dto) {
        User user = repository.findByUserName(dto.getEmail())
                .orElseThrow(() -> new LoginException(ExceptionStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다."));
        if (user.getPassword().equals(dto.getPassword())){
            return new PrincipalUserDetails(UserModel.builder()
                    .id(String.valueOf(user.getId()))
                    .name(user.getName())
                    .email(user.getEmail())
                    .roles(List.of(Role.ROLE_USER))
                    .build(), null);
        }else{
            throw new LoginException(ExceptionStatus.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }
    }

}