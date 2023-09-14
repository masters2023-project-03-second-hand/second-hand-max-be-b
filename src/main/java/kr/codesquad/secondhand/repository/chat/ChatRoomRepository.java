package kr.codesquad.secondhand.repository.chat;

import kr.codesquad.secondhand.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
