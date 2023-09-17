package kr.codesquad.secondhand.repository.chat;

import java.util.List;
import kr.codesquad.secondhand.domain.chat.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findAllByChatRoomIdOrderByIdDesc(Long chatRoomId);
}
