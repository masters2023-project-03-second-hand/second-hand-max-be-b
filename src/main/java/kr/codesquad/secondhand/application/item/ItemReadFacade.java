package kr.codesquad.secondhand.application.item;

import kr.codesquad.secondhand.presentation.dto.item.ItemDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ItemReadFacade {

    private final ItemService itemService;
    private final ViewCountService viewCountService;

    public ItemDetailResponse read(Long memberId, Long itemId) {
        ItemDetailResponse response = itemService.read(memberId, itemId);

        if (!response.getIsSeller()) {
            viewCountService.increaseViewCount(itemId);
        }

        return response;
    }
}
