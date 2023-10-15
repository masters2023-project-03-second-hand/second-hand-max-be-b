package kr.codesquad.secondhand.application.category;

import static org.assertj.core.api.Assertions.assertThat;

import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("비즈니스 로직 - 카테고리")
public class CategoryServiceTest extends ApplicationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @DisplayName("카테고리 목록을 반환한다.")
    @Test
    void whenRead_thenSuccess() {
        // given
        for (int i = 0; i < 18; i++) {
            supportRepository.save(Category.builder()
                    .name("test" + i)
                    .imageUrl("http://")
                    .build());
        }

        // when
        CategoryListResponse response = categoryService.read();

        // then
        assertThat(response.getCategories().size()).isEqualTo(18);
    }
}
