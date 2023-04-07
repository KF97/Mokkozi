package com.b303.mokkozi.report.dto;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.ReportUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReportUserDto extends BaseResponseBody {

    private Long id;
    private String reporter;
    private String target;
    private Date regDate;
    private String content;

    @Builder
    public ReportUserDto(ReportUser report,String targetEmail) {

        this.id = report.getId();
        this.content = report.getContent();
        this.regDate = report.getRegDate();
        this.reporter = report.getUser().getEmail();
        this.target = targetEmail;
    }

    public static ReportUserDto of(Integer statusCode, String message, ReportUserDto report) {
        ReportUserDto res = report;
        res.setStatusCode(statusCode);
        res.setMessage(message);
        return res;
    }

}
