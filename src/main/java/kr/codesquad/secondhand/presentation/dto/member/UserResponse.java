package kr.codesquad.secondhand.presentation.dto.member;

import java.util.List;
import lombok.Getter;

@Getter
public class UserResponse {

    private final String loginId;
    private final String profileUrl;
    private final List<AddressData> addresses;

    public UserResponse(String loginId, String profileUrl, List<AddressData> addresses) {
        this.loginId = loginId;
        this.profileUrl = profileUrl;
        this.addresses = addresses;
    }
}
