package kr.codesquad.secondhand.application.wishitem;

import kr.codesquad.secondhand.domain.WishItem;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import kr.codesquad.secondhand.repository.wishitem.WishItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WishItemService {

    private final WishItemRepository wishItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

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
