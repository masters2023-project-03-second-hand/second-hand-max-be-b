package kr.codesquad.secondhand.presentation;

import javax.validation.Valid;
import kr.codesquad.secondhand.application.category.CategoryService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import kr.codesquad.secondhand.presentation.dto.category.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<CategoryListResponse> read() {
        return new ApiResponse<>(HttpStatus.OK.value(), categoryService.read());
    }

    @PostMapping
    public ResponseEntity<Void> redirectToItemListPage(@Valid @RequestBody CategoryRequest request) {
        String redirectUrl = "/api/items?categoryId=" + request.getSelectedCategoryId();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
