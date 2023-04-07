package com.b303.mokkozi.report.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("ReportBoardPostRequest")
public class ReportBoardPostReq {

    @ApiModelProperty(name = "신고 내용", example = "신고 내용")
    private String content;

    @ApiModelProperty(name = "신고 게시글 ID")
    private Long boardId;

}