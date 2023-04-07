package com.b303.mokkozi.user.dto;

import lombok.*;

@Getter
@Setter
public class UserFollowDto {

    private Long id;

    private Long userId; // 사용자 ID

    private String nickname; // 사용자 닉네임

    private String profileUrl; // 프로필 url

    public UserFollowDto(Long id, Long userId, String nickname, String profileUrl){
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

}
