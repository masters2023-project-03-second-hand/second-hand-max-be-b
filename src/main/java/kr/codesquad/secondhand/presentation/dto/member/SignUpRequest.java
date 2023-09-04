package kr.codesquad.secondhand.presentation.dto.member;

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
    @NotBlank
    private String addressName;

    public SignUpRequest(String loginId, String addressName) {
        this.loginId = loginId;
        this.addressName = addressName;
    }

    public Member toMemberEntity(UserProfile userProfile) {
        return Member.builder()
                .loginId(this.loginId)
                .email(userProfile.getEmail())
                .profileUrl(userProfile.getProfileUrl())
                .build();
    }

    public Residence toResidenceEntity(Member member) {
        return Residence.builder()
                .addressName(this.addressName)
                .member(member)
                .build();
    }
}
