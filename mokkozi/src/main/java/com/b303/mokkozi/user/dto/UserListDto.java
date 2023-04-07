package com.b303.mokkozi.user.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.User;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;


@Getter
@Setter
@ApiModel("UserListResponse")
public class UserListDto extends BaseResponseBody {

    private Page<User> list;

    public static UserListDto of(Integer statusCode, String message, Page<User> list) {
        UserListDto res = new UserListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setList(list);
        return res;
    }

}
