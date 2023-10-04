package kr.codesquad.secondhand.presentation.dto.wishitem;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WishItemCategoryResponses {

    private final List<WishItemCategoryResponse> categories;
}
