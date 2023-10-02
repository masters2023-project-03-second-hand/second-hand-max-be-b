package kr.codesquad.secondhand.presentation.dto.item;

import java.time.LocalDateTime;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long itemId;
    private String thumbnailUrl;
    private String title;
    private String tradingRegion;
    private LocalDateTime createdAt;
    private Long price;
    private ItemStatus status;
    private int chatCount;
    private int wishCount;
}
