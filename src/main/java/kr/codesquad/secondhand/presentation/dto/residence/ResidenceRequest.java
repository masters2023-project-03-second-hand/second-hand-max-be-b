package kr.codesquad.secondhand.presentation.dto.residence;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResidenceRequest {

    @NotBlank(message = "지역의 전체 이름은 들어와야합니다.")
    private String fullAddress;

    @NotBlank(message = "읍/면/동 이름은 들어와야 합니다.")
    private String address;
}
