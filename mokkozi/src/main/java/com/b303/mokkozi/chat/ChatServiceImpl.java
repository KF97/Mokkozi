package com.b303.mokkozi.chat;

import com.b303.mokkozi.chat.dto.ChatMessageDto;
import com.b303.mokkozi.entity.ChatMessage;
import com.b303.mokkozi.entity.ChatRoom;
import com.b303.mokkozi.entity.ChatRoomJoin;
import com.b303.mokkozi.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService{

    @Autowired
    ChatRoomJoinRepository chatRoomJoinRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    UserService userService;

    @Override
    public Optional<List<ChatRoomJoin>> getChatRoomList(Long userId) {

        // 해당 유저의 채팅방 목록을 select 한 Optional 객체를 리턴
        return chatRoomJoinRepository.findByUserId(userId);
    }

    @Override
    public Page<ChatMessageDto> getChatMsgList(Long chatRoomId, int pageIdx) {

        int size = 10;
        int page = pageIdx <= 0 ? 0 : pageIdx - 1;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<ChatMessage> pageTuts = chatMessageRepository.findByChatRoomId(pageable, chatRoomId);
        Page<ChatMessageDto> chatMessageList = pageTuts.map(m -> new ChatMessageDto(m.getId(), m.getRegDate(), m.getMessage(), m.getChatRoom().getId(), m.getUser().getId()));

        return chatMessageList;
    }

    @Override
    public void insertMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public Long newRoom(Long id1, Long id2) {
        Long checkRoom = check(id1, id2); // 둘 사이의 채팅방이 존재하는지 체크하는 함수

        if(checkRoom != 0) { // 이미 채팅방이 존재한다면
            return checkRoom;
        }

        ChatRoom chatRoom = new ChatRoom();
        ChatRoom newChatRoom = chatRoomRepository.save(chatRoom);

        if(id1.equals(id2)) { // 나 자신과의 채팅방의 경우
            createRoom(id1, newChatRoom);
        } else { // 나와 상대방의 채팅인 경우
            createRoom(id1, newChatRoom);
            createRoom(id2, newChatRoom);
        }

        return newChatRoom.getId();
    }


    /*
        기존에 채팅방이 존재하는지 확인하는 함수
        존재한다면 해당 chatRoomId를 리턴한다.
     */
    private Long check(Long id1, Long id2) {
        // 각각의 user id를 기준으로 하는 채팅방 목록
        Optional<List<ChatRoomJoin>> list1 = chatRoomJoinRepository.findByUserId(id1);
        Optional<List<ChatRoomJoin>> list2 = chatRoomJoinRepository.findByUserId(id2);

        if(list1.isEmpty() || list2.isEmpty()) { // 비어있다면 아직 채팅방이 없다는 얘기
            return 0L;
        } else {
            // getList1과 getList2 사이에 공통된 roomId가 있다면 채팅방이 존재함을 알 수 있음
            List<ChatRoomJoin> getList1 = list1.get();
            List<ChatRoomJoin> getList2 = list1.get();

            for (ChatRoomJoin tmp1 : getList1) {
                for (ChatRoomJoin tmp2 : getList2) {
                    if (tmp1.getChatRoom().getId().equals(tmp2.getChatRoom().getId())) {
                        return tmp1.getChatRoom().getId(); // 해당 roomId를 리턴
                    }
                }
            }
            return 0L;
        }
    }

    private void createRoom(Long id, ChatRoom chatRoom) {
        ChatRoomJoin chatRoomJoin = new ChatRoomJoin();
        chatRoomJoin.setChatRoom(chatRoom);
        chatRoomJoin.setUser(userService.findById(id).get());
        chatRoomJoinRepository.save(chatRoomJoin);
    }

}
