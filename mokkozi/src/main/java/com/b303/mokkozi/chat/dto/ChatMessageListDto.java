package com.b303.mokkozi.chat.dto;

import com.b303.mokkozi.common.response.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@ApiModel("ChatMessageListResponse")
public class ChatMessageListDto extends BaseResponseBody {

    private Page<ChatMessageDto> chatMessageList;

    public static ChatMessageListDto of(Integer statusCode, String message, Page<ChatMessageDto> chatMessageList) {
        ChatMessageListDto res = new ChatMessageListDto();
        res.setStatusCode(statusCode);
        res.setMessage(message);
        res.setChatMessageList(chatMessageList);
        return res;
    }
}
