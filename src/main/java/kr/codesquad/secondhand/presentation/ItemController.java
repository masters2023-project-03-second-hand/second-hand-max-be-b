package kr.codesquad.secondhand.presentation;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import kr.codesquad.secondhand.application.item.ItemService;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    @PostMapping(consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<ApiResponse<Void>> registerItem(@RequestPart Optional<List<MultipartFile>> images,
                                                          @Valid @RequestPart ItemRegisterRequest item,
                                                          @Auth Long memberId) {
        itemService.register(
                images.orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_PARAMETER, "이미지는 최소 1개이상 들어와야 합니다.")),
                item,
                memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value()));
    }
}
