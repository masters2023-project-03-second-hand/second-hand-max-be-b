package kr.codesquad.secondhand.presentation.dto.item;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateRequest {

    @NotBlank(message = "상품의 제목은 비어있을 수 없습니다.")
    @Size(max = 100, message = "상품 제목의 길이는 100자를 넘을 수 없습니다.")
    private String title;

    private Long price;

    @Size(max = 2000, message = "상품 내용의 길이는 2000자를 넘을 수 없습니다.")
    private String content;

    @NotBlank(message = "상품의 판매 지역은 비어있을 수 없습니다.")
    private String region;

    @NotBlank(message = "상품의 판매 상태는 비어있을 수 없습니다.")
    private String status;

    @NotNull(message = "상품의 카테고리 아이디를 포함해 요청해주세요.")
    private Integer categoryId;

    @NotBlank(message = "상품의 카테고리 이름은 비어있을 수 없습니다.")
    private String categoryName;

    private List<String> deleteImageUrls;

    public List<String> getDeleteImageUrls() {
        if (existsDeleteImageUrls()) {
            return this.deleteImageUrls;
        }
        return List.of();
    }

    private boolean existsDeleteImageUrls() {
        return this.deleteImageUrls != null && !this.deleteImageUrls.isEmpty();
    }
}
