package kr.codesquad.secondhand.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {

    private final String email;
    private final String profileUrl;

    @Builder
    public UserProfile(String email, String profileUrl) {
        this.email = email;
        this.profileUrl = profileUrl;
    }
}
