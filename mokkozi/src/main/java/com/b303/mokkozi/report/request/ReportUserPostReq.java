package com.b303.mokkozi.report.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("ReportCreatePostRequest")
public class ReportUserPostReq {

    @ApiModelProperty(name = "신고 내용", example = "신고 내용")
    private String content;

    @ApiModelProperty(name = "타겟 사용자 ID")
    private Long targetId;

}
