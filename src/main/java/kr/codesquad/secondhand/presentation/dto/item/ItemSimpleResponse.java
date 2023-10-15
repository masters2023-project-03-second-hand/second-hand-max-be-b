package kr.codesquad.secondhand.presentation.dto.item;

import kr.codesquad.secondhand.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemSimpleResponse {

    private String title;
    private String thumbnailUrl;
    private Long price;

    public static ItemSimpleResponse from(Item item) {
        return new ItemSimpleResponse(item.getTitle(), item.getThumbnailUrl(), item.getPrice());
    }
}
