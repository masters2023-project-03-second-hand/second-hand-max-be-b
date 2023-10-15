package kr.codesquad.secondhand.application;

import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.TestContainer;
import kr.codesquad.secondhand.application.auth.KakaoRequester;
import kr.codesquad.secondhand.application.auth.NaverRequester;
import kr.codesquad.secondhand.application.image.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ApplicationTest
public abstract class ApplicationTestSupport extends TestContainer {

    @MockBean
    protected S3Uploader s3Uploader;

    @Autowired
    protected SupportRepository supportRepository;

    @MockBean
    protected NaverRequester naverRequester;

    @MockBean
    protected KakaoRequester kakaoRequester;
}
