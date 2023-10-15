package kr.codesquad.secondhand.presentation.dto.item;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatusRequest {

    @NotBlank(message = "상품의 판매 상태는 비어있을 수 없습니다.")
    private String status;
}
