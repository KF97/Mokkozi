package com.b303.mokkozi.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivePatchReq {

    @ApiModelProperty(name = "사용자 ID",example = "1")
    private Long userId;
    @ApiModelProperty(name = "사용자 활동 내용",example = "활동")
   private String active;

}
