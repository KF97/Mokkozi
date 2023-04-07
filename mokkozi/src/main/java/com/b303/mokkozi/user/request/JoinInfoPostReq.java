package com.b303.mokkozi.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@ToString
public class JoinInfoPostReq {
    @ApiModelProperty(name = "회원 ID", example = "tofan@naver.com")
    private String email;

    @ApiModelProperty(name = "닉네임", example = "팡교")
    private String nickname;

    @ApiModelProperty(name = "비밀번호", example = "ssafyb303!@#")
    private String password;

    @ApiModelProperty(name = "주소1", example = "대전광역시 서구 월평동로 83")
    private String address;

    @ApiModelProperty(name = "주소2", example = "101동 1106호")
    private String extAddress;

    @ApiModelProperty(name = "성별", example = "남")
    private String gender;

    @ApiModelProperty(name = "생년월일", example = "1995-04-12")
    private Date birth;

    @ApiModelProperty(name = "권한 정보", example = "admin / user")
    private String role;

    @ApiModelProperty(name = "사용자가 선택한 관심사", example = "['등산', '영화']")
    private ArrayList<String> hobby;


}
