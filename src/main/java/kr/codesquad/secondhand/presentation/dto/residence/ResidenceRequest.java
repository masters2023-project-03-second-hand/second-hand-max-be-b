package kr.codesquad.secondhand.presentation.dto.residence;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResidenceRequest {

    @NotNull(message = "지역의 아이디 값은 반드시 들어와야합니다.")
    private Long addressId;
}
