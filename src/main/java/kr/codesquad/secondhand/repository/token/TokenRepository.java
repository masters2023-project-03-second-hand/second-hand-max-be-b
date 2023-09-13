package kr.codesquad.secondhand.repository.token;

import kr.codesquad.secondhand.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken refreshToken WHERE refreshToken.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken refreshToken WHERE refreshToken.token = :token")
    void deleteByToken(@Param("token") String token);
}
