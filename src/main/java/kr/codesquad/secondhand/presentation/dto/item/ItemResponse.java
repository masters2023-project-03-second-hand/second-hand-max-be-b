package kr.codesquad.secondhand.presentation.dto.item;

import java.time.LocalDateTime;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import lombok.Getter;

@Getter
public class ItemResponse {

    private Long itemId;
    private String thumbnailUrl;
    private String title;
    private String tradingRegion;
    private LocalDateTime createdAt;
    private Integer price;
    private ItemStatus status;
    private int chatCount;
    private int wishCount;
}
