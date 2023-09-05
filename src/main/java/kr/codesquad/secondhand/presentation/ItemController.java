package kr.codesquad.secondhand.presentation;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import kr.codesquad.secondhand.application.item.ItemService;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemDetailResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemStatusRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> registerItem(@RequestPart Optional<List<MultipartFile>> images,
                                          @Valid @RequestPart ItemRegisterRequest item,
                                          @Auth Long memberId) {
        itemService.register(
                images.orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_PARAMETER, "이미지는 최소 1개이상 들어와야 합니다.")),
                item,
                memberId);
        return new ApiResponse<>(HttpStatus.CREATED.value());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping
    public ApiResponse<CustomSlice<ItemResponse>> readAll(
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Long categoryId,
            @RequestParam Optional<String> region,
            @RequestParam(required = false, defaultValue = "10") int size) {
        String regionName = region
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER, "상품 조회시 지역정보는 반드시 들어와야 합니다."));
        return new ApiResponse<>(HttpStatus.OK.value(), itemService.readAll(cursor, categoryId, regionName, size));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ApiResponse<ItemDetailResponse> readItem(@PathVariable Long itemId,
                                                    @Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(), itemService.read(memberId, itemId));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateItem(@RequestPart Optional<List<MultipartFile>> images,
                                        @Valid @RequestPart ItemUpdateRequest item,
                                        @PathVariable Long itemId,
                                        @Auth Long memberId) {
        itemService.update(images.orElse(null), item, itemId, memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping("/{itemId}/status")
    public ApiResponse<Void> updateItemStatus(@Valid @RequestBody ItemStatusRequest status,
                                              @PathVariable Long itemId,
                                              @Auth Long memberId) {
        itemService.updateStatus(status, itemId, memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/{itemId}")
    public ApiResponse<Void> deleteItem(@PathVariable Long itemId,
                                        @Auth Long memberId) {
        itemService.delete(itemId, memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }
}
