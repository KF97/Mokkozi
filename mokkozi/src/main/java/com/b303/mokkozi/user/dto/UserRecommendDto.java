package com.b303.mokkozi.user.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRecommendDto extends BaseResponseBody {

    private List<User> random;

    public static UserRecommendDto of(Integer statusCode, String message, List<User> random) {
        UserRecommendDto res = new UserRecommendDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setRandom(random);
        return res;
    }

}
