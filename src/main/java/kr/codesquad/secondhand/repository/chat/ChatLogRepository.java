package kr.codesquad.secondhand.repository.chat;

import kr.codesquad.secondhand.domain.chat.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatLog chatLog SET chatLog.readCount = 0 WHERE chatLog.chatRoom.id = :chatRoomId")
    void updateReadCountByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT chatLog FROM ChatLog chatLog WHERE chatLog.chatRoom.id = :chatRoomId AND chatLog.id > :id")
    List<ChatLog> findAllByChatRoom_IdAndIdIsGreaterThan(@Param("chatRoomId") Long chatRoomId, @Param("id") Long id);
}
