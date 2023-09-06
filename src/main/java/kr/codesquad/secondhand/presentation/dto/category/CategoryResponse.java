package kr.codesquad.secondhand.presentation.dto.category;

import kr.codesquad.secondhand.domain.category.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponse {

    private final Long id;
    private final String imageUrl;
    private final String name;

    @Builder
    private CategoryResponse(Long id, String imageUrl, String name) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .imageUrl(category.getImageUrl())
                .name(category.getName())
                .build();
    }
}
