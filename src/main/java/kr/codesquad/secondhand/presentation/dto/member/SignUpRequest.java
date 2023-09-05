package kr.codesquad.secondhand.presentation.dto.member;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.domain.residence.Residence;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min = 2, max = 12, message = "아이디는 2자 ~ 12자여야 합니다.")
    private String loginId;
    @Size(min = 1, max = 2, message = "주소는 최소 1개, 최대 2개까지 들어올 수 있습니다.")
    private List<String> addressNames;

    public SignUpRequest(String loginId, List<String> addressNames) {
        this.loginId = loginId;
        this.addressNames = addressNames;
    }

    public Member toMemberEntity(UserProfile userProfile) {
        return Member.builder()
                .loginId(this.loginId)
                .email(userProfile.getEmail())
                .profileUrl(userProfile.getProfileUrl())
                .build();
    }

    public List<Residence> toResidenceEntities(Member member) {
        return addressNames.stream()
                .map(addressName -> Residence.builder()
                        .addressName(addressName)
                        .member(member)
                        .build())
                .collect(Collectors.toList());
    }
}
