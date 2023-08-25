package kr.codesquad.secondhand.domain.token;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @Id
    private Long memberId;

    @Column(length = 256, nullable = false)
    private String token;

    @Builder
    private RefreshToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }
}
