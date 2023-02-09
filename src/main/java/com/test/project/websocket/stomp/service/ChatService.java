package com.test.project.websocket.stomp.service;

import com.sample.stomp.ChatMessageRepository;
import com.sample.stomp.ChatRoomRepository;
import com.sample.stomp.model.ChatRoom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

//    private Map<String, ChatRoom> chatRooms;
//@PostConstruct
//    //의존관게 주입완료되면 실행되는 코드
//    private void init() {
//        chatRooms = new LinkedHashMap<>();
//    }

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    //채팅방 불러오기
    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        return chatRoomRepository.findAll();
    }

    //채팅방 하나 불러오기
    public ChatRoom findById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    //채팅방 생성
    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
}
