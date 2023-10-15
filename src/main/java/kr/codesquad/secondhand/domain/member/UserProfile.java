package kr.codesquad.secondhand.domain.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {

    private final String email;
    private String profileUrl;

    @Builder
    public UserProfile(String email, String profileUrl) {
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public void changeProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
