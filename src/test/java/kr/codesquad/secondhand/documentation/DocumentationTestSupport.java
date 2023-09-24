package kr.codesquad.secondhand.documentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import kr.codesquad.secondhand.application.auth.AuthService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.application.category.CategoryService;
import kr.codesquad.secondhand.application.chat.ChatLogService;
import kr.codesquad.secondhand.application.chat.ChatRoomService;
import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.application.item.ItemService;
import kr.codesquad.secondhand.application.item.SalesHistoryService;
import kr.codesquad.secondhand.application.member.MemberService;
import kr.codesquad.secondhand.application.redis.RedisService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.application.wishitem.WishItemService;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.support.AuthenticationContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@MockBean({
        ChatLogService.class,
        AuthService.class,
        TokenService.class,
        CategoryService.class,
        ChatRoomService.class,
        ImageService.class,
        ItemService.class,
        MemberService.class,
        RedisService.class,
        ResidenceService.class,
        WishItemService.class,
        SalesHistoryService.class
})
@DocumentationTest
public abstract class DocumentationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtProvider jwtProvider;

    @MockBean
    protected AuthenticationContext authenticationContext;

    @BeforeEach
    void setUp() {
        given(jwtProvider.createAccessToken(anyLong())).willReturn("0pmj4.24phnepo.e32gve-3p2gjv");
        given(authenticationContext.getMemberId()).willReturn(Optional.of(1L));
    }
}
