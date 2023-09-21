package kr.codesquad.secondhand.presentation.dto.residence;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceSelectRequest {

    @NotNull(message = "선택된 거주지역은 요청에 반드시 포함되야 합니다.")
    private Long selectedAddressId;
}
