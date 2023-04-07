package com.b303.mokkozi.chat.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("ChatMsgListGetRequest")
public class ChatMsgListGetReq {

    @ApiModelProperty(name = "페이지 idx", example = "1")
    @ApiParam(value = "페이지 idx", defaultValue = "1")
    private int page;

    @ApiModelProperty(name = "페이지 size", example = "10")
    @ApiParam(value = "페이지 size", defaultValue = "10")
    private int size;
}
