package kr.codesquad.secondhand.presentation.dto.category;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @Size(min = 1, message = "카테고리 아이디는 양수여야 합니다.")
    private Long selectedCategoryId;
}
