package kr.codesquad.secondhand.repository.wishitem;

import kr.codesquad.secondhand.domain.wishitem.WishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishItemRepository extends JpaRepository<WishItem, Long> {

    @Modifying
    @Query("DELETE FROM WishItem wishItem WHERE wishItem.item.id = :itemId AND wishItem.member.id = :memberId")
    void deleteByItemIdAndMemberId(@Param("itemId") Long itemId, @Param("memberId") Long memberId);

    void deleteByItemId(Long itemId);
}
