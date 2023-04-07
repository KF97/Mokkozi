package com.b303.mokkozi.report;

import com.b303.mokkozi.entity.ReportBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportBoardRepository extends JpaRepository<ReportBoard, Long> {

    Page<ReportBoard> findByResult(Pageable pageable, String result);
}
