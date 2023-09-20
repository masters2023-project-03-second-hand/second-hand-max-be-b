package kr.codesquad.secondhand.repository.chat.querydsl;

import static kr.codesquad.secondhand.domain.chat.QChatLog.chatLog;
import static kr.codesquad.secondhand.domain.chat.QChatRoom.chatRoom;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatCountRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Long, Long> countNewMessage(Long memberId) {
        List<Tuple> results = queryFactory
                .select(chatLog.chatRoom.id,
                        chatLog.chatRoom.id.count())
                .from(chatLog)
                .join(chatLog.chatRoom, chatRoom)
                .where(isUnread()
                        .and(equalsMemberId(memberId)))
                .groupBy(chatLog.chatRoom.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),  // ChatRoomId
                        tuple -> tuple.get(1, Long.class)  // newMessageCount
                ));
    }

    private BooleanExpression isUnread() {
        return chatLog.readCount.eq(1);
    }

    private BooleanExpression equalsMemberId(Long memberId) {
        return chatLog.senderId.ne(memberId);
    }
}
