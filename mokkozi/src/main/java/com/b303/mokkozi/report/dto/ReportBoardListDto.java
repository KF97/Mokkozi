package com.b303.mokkozi.report.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ReportBoardListDto extends BaseResponseBody {

    private Page<ReportBoardDto> reportList;

    public static ReportBoardListDto of(Integer statusCode, String message, Page<ReportBoardDto> reportList) {
        ReportBoardListDto res = new ReportBoardListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setReportList(reportList);
        return res;
    }

}
