package kr.codesquad.secondhand.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.application.auth.NaverRequester;
import kr.codesquad.secondhand.application.image.S3Uploader;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@AcceptanceTest
public abstract class AcceptanceTestSupport {

    @MockBean
    protected S3Uploader s3Uploader;

    @MockBean
    protected NaverRequester naverRequester;

    @Autowired
    protected SupportRepository supportRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtProvider jwtProvider;

    protected Member signup() {
        return supportRepository.save(FixtureFactory.createMember());
    }
}
