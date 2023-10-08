package kr.codesquad.secondhand.presentation.support.converter;

import kr.codesquad.secondhand.domain.member.OAuthProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OAuthProviderConverter implements Converter<String, OAuthProvider> {

    @Override
    public OAuthProvider convert(String oAuthProvider) {
        return OAuthProvider.of(oAuthProvider);
    }
}
