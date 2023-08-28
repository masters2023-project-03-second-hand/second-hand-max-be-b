package kr.codesquad.secondhand.application.category;

import static org.assertj.core.api.Assertions.assertThat;

import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SupportRepository supportRepository;

    @Disabled
    @DisplayName("카테고리 목록을 반환한다.")
    @Test
    void whenRead_thenSuccess() {
        // when
        CategoryListResponse response = categoryService.read();

        // then
        assertThat(response.getCategories().size()).isEqualTo(18);
    }
}
