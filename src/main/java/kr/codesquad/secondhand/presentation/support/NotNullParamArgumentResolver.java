package kr.codesquad.secondhand.presentation.support;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class NotNullParamArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(NotNullParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        NotNullParam annotation = parameter.getParameterAnnotation(NotNullParam.class);
        String queryParamKey = annotation.key();
        if (!StringUtils.hasText(queryParamKey)) {
            queryParamKey = parameter.getParameterName();
        }
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        return Optional.ofNullable(request.getParameter(queryParamKey))
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER, annotation.message()));
    }
}
