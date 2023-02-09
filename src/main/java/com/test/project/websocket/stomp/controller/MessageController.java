package com.test.project.websocket.stomp.controller;

import com.sample.stomp.ChatMessageRepository;
import com.sample.stomp.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;
    private final ChatMessageRepository chatMessageRepository;

    // Client 가 send 할 수 있는 경로 WebsocketConfig 에서 등록한
    //applicationDestinationPrefixes 와 @MessageMapping의 경로가 합쳐진다
    //
    @MessageMapping("/chat/message")
    public void enter(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 입장하였습니다.");
        } else chatMessageRepository.save(message);

        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}

