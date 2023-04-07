package com.b303.mokkozi.board;

import com.b303.mokkozi.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findById(long id);

    Page<Board> findByActiveLike(Pageable pageable, String active);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findByUserIdContaining(Pageable pageable, String keyword);

//    Page<Board> findByTitleContaining(Pageable pageable, String keyword);
}
