package kr.codesquad.secondhand.presentation.dto.wishitem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WishItemCategoryResponse {

    private final Long categoryId;
    private final String categoryName;
}
