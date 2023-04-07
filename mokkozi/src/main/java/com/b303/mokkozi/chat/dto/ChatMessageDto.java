package com.b303.mokkozi.chat.dto;

import com.b303.mokkozi.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageDto {

    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date regDate;
    private String message;
    private Long roomId;
    private Long userId;

    @Builder
    public ChatMessageDto(Long id, Date regDate, String message, Long roomId, Long userId) {
        this.id = id;
        this.regDate = regDate;
        this.message = message;
        this.roomId = roomId;
        this.userId = userId;
    }
}
