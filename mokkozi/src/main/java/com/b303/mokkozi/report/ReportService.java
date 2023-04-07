package com.b303.mokkozi.report;

import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.report.dto.ReportBoardDto;
import com.b303.mokkozi.report.dto.ReportUserDto;
import com.b303.mokkozi.report.request.ReportBoardPostReq;
import com.b303.mokkozi.report.request.ReportUserPostReq;
import com.b303.mokkozi.report.request.ReportBoardUpdateReq;
import org.springframework.data.domain.Page;

public interface ReportService {

    void createUserReport(User user, ReportUserPostReq rcpr);

    Page<ReportUserDto> getUserReportList(int page);

    ReportUserDto getUserReport(Long reportId);

//    void updateUserReport(ReportBoardUpdateReq ruur);

    void createBoardReport(User user, ReportBoardPostReq rbpr);

    Page<ReportBoardDto> getBoardReportList(int page, String result);

    void updateBoardReport(ReportBoardUpdateReq rbur);

    ReportBoardDto getBoardReport(Long reportId);
}
