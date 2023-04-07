package com.b303.mokkozi.chat;

import com.b303.mokkozi.chat.dto.ChatMessageDto;
import com.b303.mokkozi.chat.request.ChatMsgListGetReq;
import com.b303.mokkozi.entity.ChatMessage;
import com.b303.mokkozi.entity.ChatRoomJoin;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Optional<List<ChatRoomJoin>> getChatRoomList(Long userId);

    Page<ChatMessageDto> getChatMsgList(Long chatRoomId, int page);

    void insertMessage(ChatMessage chatMessage);

    Long newRoom(Long id1, Long id2);
}
