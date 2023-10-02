package kr.codesquad.secondhand.application.wishitem;

import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.application.item.PagingUtils;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.wishitem.WishItemCategoryResponse;
import kr.codesquad.secondhand.presentation.dto.wishitem.WishItemCategoryResponses;
import kr.codesquad.secondhand.presentation.support.converter.IsWish;
import kr.codesquad.secondhand.repository.category.CategoryRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import kr.codesquad.secondhand.repository.wishitem.WishItemRepository;
import kr.codesquad.secondhand.repository.wishitem.querydsl.WishItemCategoryRepository;
import kr.codesquad.secondhand.repository.wishitem.querydsl.WishItemPaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WishItemService {

    private final ItemRepository itemRepository;
    private final WishItemRepository wishItemRepository;
    private final CategoryRepository categoryRepository;
    private final WishItemPaginationRepository wishItemPaginationRepository;
    private final WishItemCategoryRepository wishItemCategoryRepository;

    @Transactional
    public void changeWishStatusOfItem(Long itemId, Long memberId, IsWish isWish) {
        if (isWish == IsWish.YES) {
            registerWishItem(itemId, memberId);
            return;
        }
        removeWishItem(itemId, memberId);
    }

    private void registerWishItem(Long itemId, Long memberId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND,
                        String.format("%s 번호의 아이템을 찾을 수 없습니다.", itemId)));
        item.increaseWishCount();

        wishItemRepository.save(WishItem.of(itemId, memberId));
    }

    private void removeWishItem(Long itemId, Long memberId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> NotFoundException.itemNotFound(ErrorCode.NOT_FOUND, itemId));
        item.decreaseWishCount();

        wishItemRepository.deleteByItemIdAndMemberId(itemId, memberId);
    }

    public CustomSlice<ItemResponse> readAll(Long memberId, Long categoryId, Long cursor, int pageSize) {
        String categoryName = categoryRepository.findNameById(categoryId).orElse(null);
        Slice<ItemResponse> itemResponses =
                wishItemPaginationRepository.findAll(memberId, cursor, categoryName, pageSize);

        List<ItemResponse> content = itemResponses.getContent();

        Long nextCursor = PagingUtils.setNextCursor(content, itemResponses.hasNext());

        return new CustomSlice<>(content, nextCursor, itemResponses.hasNext());
    }

    public WishItemCategoryResponses readCategories(Long memberId) {
        List<String> categoryNames = wishItemCategoryRepository.findCategoryNameByMemberId(memberId);
        return new WishItemCategoryResponses(categoryNames.stream()
                .map(name -> new WishItemCategoryResponse(categoryRepository.findIdByName(name), name))
                .collect(Collectors.toList()));
    }
}
