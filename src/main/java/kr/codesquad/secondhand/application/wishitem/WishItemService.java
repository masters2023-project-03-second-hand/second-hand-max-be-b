package kr.codesquad.secondhand.application.wishitem;

import java.util.List;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.repository.category.CategoryRepository;
import kr.codesquad.secondhand.repository.wishitem.WishItemRepository;
import kr.codesquad.secondhand.repository.wishitem.querydsl.WishItemPaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WishItemService {

    private final WishItemRepository wishItemRepository;
    private final CategoryRepository categoryRepository;
    private final WishItemPaginationRepository wishItemPaginationRepository;

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

    public CustomSlice<ItemResponse> readAll(Long memberId, Long categoryId, Long cursor, int pageSize) {
        String categoryName = categoryRepository.findNameById(categoryId).orElse(null);
        Slice<ItemResponse> itemResponses =
                wishItemPaginationRepository.findAll(memberId, cursor, categoryName, pageSize);

        List<ItemResponse> content = itemResponses.getContent();

        Long nextCursor = setNextCursor(content, itemResponses.hasNext());

        return new CustomSlice<>(content, nextCursor, itemResponses.hasNext());
    }

    private Long setNextCursor(List<ItemResponse> content, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext) {
            nextCursor = content.get(content.size() - 1).getItemId();
        }
        return nextCursor;
    }
}
