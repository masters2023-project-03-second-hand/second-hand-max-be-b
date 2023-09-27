package kr.codesquad.secondhand.application.item;

import java.util.List;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.repository.item.querydsl.ItemPaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SalesHistoryService {

    private final ItemPaginationRepository itemPaginationRepository;

    public CustomSlice<ItemResponse> read(Long itemId, String status, int pageSize, Long memberId) {
        ItemStatus itemStatus = null;
        if (!status.equals("all")) {
            itemStatus = ItemStatus.fromEnglish(status);
        }
        Slice<ItemResponse> response =
                itemPaginationRepository.findByIdAndStatus(itemId, itemStatus, pageSize, memberId);

        List<ItemResponse> content = response.getContent();

        Long nextCursor = PagingUtils.setNextCursor(content, response.hasNext());

        return new CustomSlice<>(content, nextCursor, response.hasNext());
    }
}
