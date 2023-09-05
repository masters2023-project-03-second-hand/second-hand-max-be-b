package kr.codesquad.secondhand.application.wishitem;

import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.repository.wishitem.WishItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WishItemService {

    private final WishItemRepository wishItemRepository;

    @Transactional
    public void registerWishItem(Long itemId, Long memberId) {
        wishItemRepository.save(WishItem.builder()
                .item(Item.builder()
                        .id(itemId)
                        .build())
                .member(Member.builder()
                        .id(memberId)
                        .build())
                .build());
    }

    @Transactional
    public void removeWishItem(Long itemId, Long memberId) {
        wishItemRepository.deleteByItemIdAndMemberId(itemId, memberId);
    }
}
