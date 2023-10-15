package kr.codesquad.secondhand.presentation.support;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthenticationContext {

    private Long memberId;

    public Optional<Long> getMemberId() {
        return Optional.ofNullable(memberId);
    }

    public void setMemberId(Map<String, Object> claims) {
        this.memberId = Long.valueOf(claims.get("memberId").toString());
    }
}
