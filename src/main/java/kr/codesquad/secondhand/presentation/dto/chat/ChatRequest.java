package kr.codesquad.secondhand.presentation.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ChatRequest {

    @NotNull(message = "채팅 메시지는 null 일 수 없습니다.")
    private String message;
}
