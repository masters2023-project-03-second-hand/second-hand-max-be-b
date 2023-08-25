package kr.codesquad.secondhand.repository.token;

import kr.codesquad.secondhand.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
}
