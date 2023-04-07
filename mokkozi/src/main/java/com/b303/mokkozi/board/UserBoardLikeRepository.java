package com.b303.mokkozi.board;

import com.b303.mokkozi.entity.UserBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBoardLikeRepository extends JpaRepository<UserBoardLike, Long> {

    Optional<UserBoardLike> findByUserIdAndBoardId(Long id, Long boardId);

    long countByBoardId(Long id);
}
