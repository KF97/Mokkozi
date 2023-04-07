package com.b303.mokkozi.report.dto;

import com.b303.mokkozi.board.dto.BoardDto;
import com.b303.mokkozi.board.dto.BoardListDto;
import com.b303.mokkozi.common.response.BaseResponseBody;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ReportUserListDto extends BaseResponseBody {

    private Page<ReportUserDto> reportList;

    public static ReportUserListDto of(Integer statusCode, String message, Page<ReportUserDto> reportList) {
        ReportUserListDto res = new ReportUserListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setReportList(reportList);
        return res;
    }


}
