package kr.codesquad.secondhand.application.item.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemViewEvent {

    private final Long itemId;
}
