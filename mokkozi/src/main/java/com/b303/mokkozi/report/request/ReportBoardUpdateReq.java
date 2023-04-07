package com.b303.mokkozi.report.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportBoardUpdateReq {

    @ApiModelProperty(name = "게시글 신고 ID", example = "1")
    private Long reportId;
    @ApiModelProperty(name = "신고 결과", example = "신고 결과")
    private String result;
}
