package com.test.project.websocket.stomp;

import com.sample.stomp.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

//    List<ChatRoom> findAllOrderById();

    ChatRoom findByRoomId(String roomId);

}
