package com.bangez.user.service;


import com.bangez.user.domain.dto.*;
import org.springframework.stereotype.Service;

public interface OAuthService {

    PrincipalUserDetails login(LoginDto dto);


    LoginDto oauthJoin(OAuth2UserDto dto);

}