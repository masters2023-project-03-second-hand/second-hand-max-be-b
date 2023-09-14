package kr.codesquad.secondhand.presentation.dto.chat;

import java.util.List;
import kr.codesquad.secondhand.presentation.dto.item.ItemSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatLogResponse {

    private String chatPartnerName;
    private ItemSimpleResponse item;
    private List<SimpleChatLog> chat;
}
