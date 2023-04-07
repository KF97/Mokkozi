package com.b303.mokkozi.chat;

import com.b303.mokkozi.entity.ChatMessage;
import com.b303.mokkozi.entity.ChatRoomJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomJoinRepository extends JpaRepository<ChatRoomJoin, Long> {

    Optional<List<ChatRoomJoin>> findByUserId(Long userId);

}
