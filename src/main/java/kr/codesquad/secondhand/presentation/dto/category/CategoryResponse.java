package kr.codesquad.secondhand.presentation.dto.category;

import kr.codesquad.secondhand.domain.category.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponse {

    private final Long id;
    private final String imageUrl;
    private final String name;
    private final boolean selected;

    @Builder
    private CategoryResponse(Long id, String imageUrl, String name, boolean selected) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.selected = selected;
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .imageUrl(category.getImageUrl())
                .name(category.getName())
                .selected(false)
                .build();
    }
}
