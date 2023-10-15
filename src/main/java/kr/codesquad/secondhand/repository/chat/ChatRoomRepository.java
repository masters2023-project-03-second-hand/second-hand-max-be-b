package kr.codesquad.secondhand.repository.chat;

import kr.codesquad.secondhand.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT chatRoom.id FROM ChatRoom chatRoom WHERE chatRoom.item.id = :itemId AND chatRoom.buyer.id = :buyerId")
    Optional<Long> findByItem_IdAndBuyer_Id(@Param("itemId") Long itemId, @Param("buyerId") Long buyerId);
}
