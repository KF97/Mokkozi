package com.b303.mokkozi.chat;

import com.b303.mokkozi.chat.dto.ChatMessageDto;
import com.b303.mokkozi.chat.dto.ChatMessageListDto;
import com.b303.mokkozi.chat.request.ChatMsgListGetReq;
import com.b303.mokkozi.common.response.BaseResponseBody;
import com.b303.mokkozi.entity.ChatMessage;
import com.b303.mokkozi.entity.ChatRoomJoin;
import com.b303.mokkozi.entity.User;
import com.b303.mokkozi.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meet/chat")
public class ChatController {

//    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

//    private final SimpMessagingTemplate template;

    // 채팅방 목록 조회
    @GetMapping("/me")
    @ApiOperation(value = "채팅방 목록 조회", notes = "내가 참여한 채팅방 목록을 조회한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<Optional<List<ChatRoomJoin>>> getChatRoomList(@ApiIgnore Authentication authentication) {
        // jwt 토큰 값을  파라미터로 받아와서 인증 및 해당 유저의 id를 확인
        User user = (User) authentication.getDetails();
        Long id = user.getId();

        // 해당 id를 기준으로 채팅방 목록을 조회하여 리턴한다.
        return new ResponseEntity<>(chatService.getChatRoomList(id), HttpStatus.OK);
    }

    // 해당 채팅방의 메시지 목록을 불러오기
    @GetMapping("/me/{chatRoomId}")
    @ApiOperation(value = "해당 채팅방의 메시지 목록 조회", notes = "메시지 목록을 조회한다.")
    @ApiResponses({@ApiResponse(code = 200, message = "성공"), @ApiResponse(code = 400, message = "실패"),
            @ApiResponse(code = 401, message = "로그인 인증 실패"), @ApiResponse(code = 403, message = "잘못된 요청")})
    public ResponseEntity<? extends BaseResponseBody> getChatMsgList(
            @PathVariable Long chatRoomId,
            @RequestParam @ApiParam(value = "채팅 메시지 페이지 Index", defaultValue = "0") int page) {

        // 해당 채팅방 id를 기준으로 메시지를 탐색해서 리턴
        Page<ChatMessageDto> messageList = chatService.getChatMsgList(chatRoomId, page);
        return ResponseEntity.ok(ChatMessageListDto.of(200, "채팅 메시지 조회 완료", messageList));
    }

//    // 채팅 메시지 전송
//    @MessageMapping("/send")
//    public void sendMsg(@Payload ChatMessage chatMessage) {
//        chatService.insertMessage(chatMessage); // db에 메시지를 저장
//        template.convertAndSend("/sub/" + chatMessage.getChatRoom().getId(), chatMessage); // 해당 목적지에 메시지를 전송
//    }

    // chat (delete) - 채팅방 삭제(나가기)

    // chat (post) - 채팅방 만들기
    @PostMapping("")
    public Long create(@ApiIgnore Authentication authentication, @RequestParam @ApiParam(value = "상대방 email") String email) {
        // 내 id
        User user1 = (User) authentication.getDetails();
        Long id1 = user1.getId();

        // 상대방 id
        Optional<User> user2 = userService.findByEmail(email);
        Long id2 = user2.get().getId();

        // 내 id 와 상대방 id 를 기준으로 chat_room 을 하나 생성
        Long chatRoomId = chatService.newRoom(id1, id2);
        // 생성된 chat_room 을 FK로 하여 상대방과 내 ID를 컬럼값으로 갖는 데이터를 저장한다.

        return chatRoomId;
    }

//    @GetMapping("/alarm/stomp")
//    @ApiOperation(value = "Stomp 테스트")
//    public String stompAlarm() {
//        return "/stomp";
//    }
//
//    @MessageMapping("/{userId")
//    public void message(@DestinationVariable("userId") Long userId) {
//        messagingTemplate.convertAndSend("/sub/" + userId, "alarm socket connection completed.");
//    }
}
