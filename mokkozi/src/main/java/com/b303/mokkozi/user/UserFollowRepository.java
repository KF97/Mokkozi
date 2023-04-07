package com.b303.mokkozi.user;

import com.b303.mokkozi.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow,Long> {

//    List<UserFollow> getByUserFollower(Long id);

//    Optional<List<UserFollow>> findAllByToUserId(Long id);

    Stream<UserFollow> findAllByFromUserId(Long id);

    int countByToUserId(Long id);

    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    Page<UserFollow> findByToUserId(Pageable pageable, Long id);

    int countByFromUserId(Long id);

    Page<UserFollow> findByFromUserId(Pageable pageable, Long id);
}
