package kr.codesquad.secondhand.domain.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String loginId;

    @Column(length = 45, nullable = false)
    private String email;

    @Column(length = 512, nullable = false)
    private String profileUrl;

    @Builder
    private Member(Long id, String loginId, String email, String profileUrl) {
        this.id = id;
        this.loginId = loginId;
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }

    public void changeProfileImage(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
