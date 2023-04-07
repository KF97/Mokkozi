package com.b303.mokkozi.report;

import com.b303.mokkozi.entity.ReportUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportUserRepository extends JpaRepository<ReportUser, Long> {
    String findEmailById(Long targetId);

}
