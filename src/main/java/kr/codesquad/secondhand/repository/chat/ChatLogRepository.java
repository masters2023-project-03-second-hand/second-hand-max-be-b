package kr.codesquad.secondhand.repository.chat;

import java.util.List;
import kr.codesquad.secondhand.domain.chat.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findAllByChatRoomId(Long chatRoomId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatLog chatLog SET chatLog.readCount = 0 WHERE chatLog.chatRoom.id = :chatRoomId")
    void updateReadCountByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
