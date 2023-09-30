package kr.codesquad.secondhand.presentation;

import java.util.List;
import kr.codesquad.secondhand.application.wishitem.WishItemService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.wishitem.WishItemCategoryResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import kr.codesquad.secondhand.presentation.support.converter.IsWish;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/wishes")
@RestController
public class WishItemController {

    private final WishItemService wishItemService;

    @PostMapping("/{itemId}")
    public ApiResponse<Void> changeWishStatusOfItem(@RequestParam("wish") IsWish isWish,
                                                    @PathVariable Long itemId,
                                                    @Auth Long memberId) {
        wishItemService.changeWishStatusOfItem(itemId, memberId, isWish);
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @GetMapping
    public ApiResponse<CustomSlice<ItemResponse>> readAll(@RequestParam(required = false) Long categoryId,
                                                          @RequestParam(required = false) Long cursor,
                                                          @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                          @Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(),
                wishItemService.readAll(memberId, categoryId, cursor, pageSize));
    }

    @GetMapping("/categories")
    public ApiResponse<List<WishItemCategoryResponse>> readCategories(@Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(), wishItemService.readCategories(memberId));
    }
}
