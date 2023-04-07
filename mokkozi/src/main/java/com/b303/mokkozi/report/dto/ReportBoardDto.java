package com.b303.mokkozi.report.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.ReportBoard;
import com.b303.mokkozi.entity.ReportUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReportBoardDto extends BaseResponseBody {


    private Long id;
    private Long boardId;
    private String result;
    private Date regDate;
    private String content;

    @Builder
    public ReportBoardDto(ReportBoard report) {

        this.id = report.getId();
        this.content = report.getContent();
        this.regDate = report.getRegDate();
        this.result = report.getResult();
        this.boardId = report.getBoard().getId();
    }

    public static ReportBoardDto of(Integer statusCode, String message, ReportBoardDto report) {
        ReportBoardDto res = report;
        res.setStatusCode(statusCode);
        res.setMessage(message);
        return res;
    }

}
