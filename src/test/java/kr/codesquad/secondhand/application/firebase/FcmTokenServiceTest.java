package kr.codesquad.secondhand.application.firebase;

import static org.assertj.core.api.Assertions.assertThat;

import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class FcmTokenServiceTest extends ApplicationTestSupport {

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DisplayName("토큰을 저장하는데 성공한다.")
    @Test
    void givenTokenValue_whenUpdateToken_thenSuccess() {
        // given
        Member member = supportRepository.save(FixtureFactory.createMember());
        String tokenValue = "testTokenValue";

        // when
        fcmTokenService.updateToken(tokenValue, member.getId());

        // then
        String token = (String) redisTemplate.opsForValue().get("fcm_token:" + member.getId());
        assertThat(token).isNotBlank();
    }
}
