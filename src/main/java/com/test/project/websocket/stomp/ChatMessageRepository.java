package com.test.project.websocket.stomp;

import com.sample.stomp.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long > {
}
