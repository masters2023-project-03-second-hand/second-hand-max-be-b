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
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_room")
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String subject;

    @Column(nullable = false)
    private LocalDateTime lastSendTime;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @JoinColumn(nullable = false, name = "buyer_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member buyer;

    @JoinColumn(nullable = false, name = "seller_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @JoinColumn(nullable = false, name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Builder
    private ChatRoom(Long id, String subject, Member buyer, Member seller, Item item) {
        this.id = id;
        this.subject = subject;
        this.lastSendTime = LocalDateTime.now();
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }

    public static ChatRoom of(Long creatorId, Long itemId, Long sellerId) {
        return ChatRoom.builder()
                .subject("")
                .item(Item.builder()
                        .id(itemId)
                        .build())
                .buyer(Member.builder()
                        .id(creatorId)
                        .build())
                .seller(Member.builder()
                        .id(sellerId)
                        .build())
                .build();
    }
}
