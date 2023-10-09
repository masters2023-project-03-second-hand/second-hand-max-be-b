package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.category.CategoryService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
}
