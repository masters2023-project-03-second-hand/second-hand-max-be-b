package kr.codesquad.secondhand.presentation.support.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IsWishRequestConverter implements Converter<String, IsWish> {

    @Override
    public IsWish convert(String isWish) {
        return IsWish.of(isWish);
    }
}
