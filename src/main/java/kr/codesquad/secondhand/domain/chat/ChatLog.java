package kr.codesquad.secondhand.domain.chat;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_log")
@Entity
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, name = "read_count")
    private Integer readCount;

    @Column(nullable = false, name = "sender_id")
    private Long senderId;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @JoinColumn(nullable = false, name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Builder
    private ChatLog(Long id, String message, Integer readCount, Long senderId, ChatRoom chatRoom) {
        this.id = id;
        this.message = message;
        this.readCount = readCount;
        this.senderId = senderId;
        this.chatRoom = chatRoom;
    }

    public static ChatLog of(ChatRoom chatRoom, String message, Integer readCount, Long senderId) {
        return ChatLog.builder()
                .message(message)
                .readCount(readCount)
                .senderId(senderId)
                .chatRoom(chatRoom)
                .build();
    }
}
