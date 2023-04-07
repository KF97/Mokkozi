package com.b303.mokkozi.report;

import com.b303.mokkozi.board.BoardRepository;
import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.ReportBoard;
import com.b303.mokkozi.entity.ReportUser;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.report.dto.ReportBoardDto;
import com.b303.mokkozi.report.dto.ReportUserDto;
import com.b303.mokkozi.report.request.ReportBoardPostReq;
import com.b303.mokkozi.report.request.ReportBoardUpdateReq;
import com.b303.mokkozi.report.request.ReportUserPostReq;
import com.b303.mokkozi.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportUserRepository ruRepository;

    @Autowired
    ReportBoardRepository rbRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    /* 사용자 신고 */

    @Override
    public void createUserReport(User user, ReportUserPostReq rcpr) {

        User target = userRepository.findById(rcpr.getTargetId()).orElseThrow(() -> new NoSuchElementException("not found"));

        ReportUser report = new ReportUser();
        report.setContent(rcpr.getContent());
        report.setUser(user);
        report.setTargetId(rcpr.getTargetId());
        ruRepository.save(report);

        long pc = target.getPenaltyCount() + 1;
        target.setPenaltyCount(pc);

        if(target.getPenaltyCount()==(long)5)
            target.setActive("정지");

        userRepository.save(target);

    }

    @Override
    public Page<ReportUserDto> getUserReportList(int pageIdx) {

        int size = 10;
        int page = pageIdx <= 0 ? 0 : pageIdx - 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));

        Page<ReportUser> pageTuts = ruRepository.findAll(pageable);
        Page<ReportUserDto> reportList = pageTuts.map(m -> new ReportUserDto(m, userRepository.findById(m.getTargetId()).get().getEmail()));
        return reportList;


    }

    @Override
    public ReportUserDto getUserReport(Long reportId) {
        ReportUser report = ruRepository.findById(reportId).orElseThrow(() -> new NoSuchElementException("not found"));
        ReportUserDto reportDto = new ReportUserDto(report, userRepository.findById(report.getTargetId()).get().getEmail());
        return reportDto;
    }


    /* 게시글 신고 */

    @Override
    public void createBoardReport(User user, ReportBoardPostReq rbpr) {

        Board board = boardRepository.findById(rbpr.getBoardId()).orElseThrow(() -> new NoSuchElementException("not found"));

        ReportBoard report = new ReportBoard();
        report.setContent(rbpr.getContent());
        report.setResult("대기중");
        report.setBoard(board);
        rbRepository.save(report);
    }

    @Override
    public Page<ReportBoardDto> getBoardReportList(int pageIdx, String result) {
        int size = 10;
        int page = pageIdx <= 0 ? 0 : pageIdx - 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));

        if (result.equals("")) {
            Page<ReportBoard> pageTuts = rbRepository.findAll(pageable);
            Page<ReportBoardDto> reportList = pageTuts.map(m -> new ReportBoardDto(m));
            return reportList;
        } else {
            Page<ReportBoard> pageTuts = rbRepository.findByResult(pageable, result);
            Page<ReportBoardDto> reportList = pageTuts.map(m -> new ReportBoardDto(m));
            return reportList;
        }
    }

    @Override
    public ReportBoardDto getBoardReport(Long reportId) {
        ReportBoard report = rbRepository.findById(reportId).orElseThrow(() -> new NoSuchElementException("not found"));
        ReportBoardDto reportDto = new ReportBoardDto(report);
        return reportDto;
    }

    @Override
    public void updateBoardReport(ReportBoardUpdateReq rbur) {
        ReportBoard report = rbRepository.findById(rbur.getReportId()).orElseThrow(() -> new NoSuchElementException("not found"));
        report.setResult(rbur.getResult());
        rbRepository.save(report);
    }

}
