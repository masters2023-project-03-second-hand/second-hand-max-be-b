package kr.codesquad.secondhand.domain.member;

import java.io.IOException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserProfile {

    private final String email;
    private String profileUrl;

    @Builder
    public UserProfile(String email, String profileUrl) {
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
