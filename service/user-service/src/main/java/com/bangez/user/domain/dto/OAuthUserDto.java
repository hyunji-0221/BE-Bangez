package com.bangez.user.domain.dto;

import com.bangez.user.domain.vo.Registration;
import com.bangez.user.domain.vo.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OAuthUserDto {
    private String id;
    private String email;
    private String name;
    private String profile;
    private List<Role> roles;

    private Registration registration;
    private String oauthId;
}
