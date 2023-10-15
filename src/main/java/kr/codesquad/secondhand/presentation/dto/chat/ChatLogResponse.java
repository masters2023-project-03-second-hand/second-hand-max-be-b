package kr.codesquad.secondhand.presentation.dto.chat;

import kr.codesquad.secondhand.presentation.dto.item.ItemSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatLogResponse {

    private String chatPartnerName;
    private ItemSimpleResponse item;
    private List<SimpleChatLog> chat;
    private Long nextMessageId;
}
