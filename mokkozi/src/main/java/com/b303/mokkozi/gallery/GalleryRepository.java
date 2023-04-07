package com.b303.mokkozi.gallery;

import com.b303.mokkozi.entity.Board;
import com.b303.mokkozi.entity.Gallery;
import com.b303.mokkozi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    Optional<List<Gallery>> findByUser(User user);

    List<Gallery> findAllByBoardId(Long boardId);

    List<Gallery> findAllByUserId(Long userId);

}
