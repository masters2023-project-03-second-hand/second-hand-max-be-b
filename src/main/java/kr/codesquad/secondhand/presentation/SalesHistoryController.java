package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.item.SalesHistoryService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/sales/history")
@RestController
public class SalesHistoryController {

    private final SalesHistoryService salesHistoryService;

    @GetMapping
    public ApiResponse<CustomSlice<ItemResponse>> readHistory(@RequestParam(defaultValue = "all") String status,
                                                              @RequestParam(required = false) Long cursor,
                                                              @RequestParam(required = false, defaultValue = "10") int size,
                                                              @Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(), salesHistoryService.read(cursor, status, size, memberId));
    }
}
