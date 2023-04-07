package com.b303.mokkozi.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialPostReq {
    @ApiModelProperty(name = "회원 ID", example = "tofan@naver.com")
    private String email;
    @ApiModelProperty(name = "회원 비밀번호", example = "ssafyb303!@#")
    private String password;
}
