package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.wishitem.WishItemService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/wishes")
@RestController
public class WishItemController {

    private final WishItemService wishItemService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/{itemId}")
    public ApiResponse<Void> changeWishStatusOfItem(@RequestParam("wish") String isWish,
                                                    @PathVariable Long itemId,
                                                    @Auth Long memberId) {
        if (isWish.equals("yes")) {
            wishItemService.registerWishItem(itemId, memberId);
            return new ApiResponse<>(HttpStatus.OK.value());
        }
        wishItemService.removeWishItem(itemId, memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }
}
