package kr.codesquad.secondhand.presentation.interceptor;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.presentation.support.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class RegionNotLoginInterceptor implements HandlerInterceptor {

    private static final Long NOT_LOGIN_MEMBER_ID = -1L;
    private static final String NOT_LOGIN_DEFAULT_REGION = "역삼1동";

    private final AuthenticationContext authenticationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }

        String region = Optional.ofNullable(request.getParameter("region"))
                .orElse("역삼1동");

        Long memberId = authenticationContext.getMemberId()
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.NOT_LOGIN));
        if (!region.equals(NOT_LOGIN_DEFAULT_REGION) && NOT_LOGIN_MEMBER_ID.equals(memberId)) {
//            throw new UnAuthorizedException(ErrorCode.NOT_LOGIN, "로그인되지 않은 상태에서는 역삼 1동 지역만을 볼 수 있습니다.");
        }
        return true;
    }
}
