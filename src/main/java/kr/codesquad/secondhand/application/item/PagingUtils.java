package kr.codesquad.secondhand.application.item;

import java.util.List;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;

public final class PagingUtils {

    private PagingUtils() {
    }

    public static Long setNextCursor(List<ItemResponse> content, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext) {
            nextCursor = content.get(content.size() - 1).getItemId();
        }
        return nextCursor;
    }

    public static Long setNextCursorForChatRoom(List<ChatRoomResponse> content, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext) {
            nextCursor = content.get(content.size() - 1).getChatRoomId();
        }
        return nextCursor;
    }
}
