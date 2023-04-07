package com.b303.mokkozi.user.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("UserFollowersResponse")
public class UserFollowListDto extends BaseResponseBody {

    private List<UserFollowDto> followers;

    public static UserFollowListDto of(Integer statusCode, String message, List<UserFollowDto> followers) {
        UserFollowListDto res = new UserFollowListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setFollowers(followers);
        return res;
    }
}
