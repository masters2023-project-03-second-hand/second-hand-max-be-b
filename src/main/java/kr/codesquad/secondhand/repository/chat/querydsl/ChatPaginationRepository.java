package kr.codesquad.secondhand.repository.chat.querydsl;

import static kr.codesquad.secondhand.domain.chat.QChatRoom.chatRoom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import kr.codesquad.secondhand.repository.PaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatPaginationRepository implements PaginationRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<ChatRoomResponse> findByMemberId(Long memberId, Long chatRoomId, int pageSize) {
        Expression<String> loginIdExpression = createPartnerNameExpression(memberId);
        Expression<String> profileExpression = createPartnerProfileExpression(memberId);

        List<ChatRoomResponse> chatRoomResponses = queryFactory
                .select(Projections.fields(ChatRoomResponse.class,
                        chatRoom.id.as("chatRoomId"),
                        chatRoom.item.thumbnailUrl,
                        chatRoom.lastSendTime,
                        chatRoom.subject.as("lastSendMessage"),
                        loginIdExpression,
                        profileExpression))
                .from(chatRoom)
                .where(beforeThanId(chatRoomId),
                        equalsMemberId(memberId)
                )
                .orderBy(chatRoom.lastSendTime.desc())
                .limit(pageSize + 1)
                .fetch();
        return checkLastPage(pageSize, chatRoomResponses);
    }

    private Expression<String> createPartnerNameExpression(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.sender.id.eq(memberId))
                .then(chatRoom.receiver.loginId)
                .otherwise(chatRoom.sender.loginId)
                .as("chatPartnerName");
    }

    private Expression<String> createPartnerProfileExpression(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.sender.id.eq(memberId))
                .then(chatRoom.receiver.profileUrl)
                .otherwise(chatRoom.sender.profileUrl)
                .as("chatPartnerProfile");
    }

    private JPQLQuery<LocalDateTime> findLastSendTimeById(Long chatRoomId) {
        return JPAExpressions
                .select(chatRoom.lastSendTime)
                .from(chatRoom)
                .where(chatRoom.id.eq(chatRoomId));
    }

    private BooleanExpression beforeThanId(Long chatRoomId) {
        if (chatRoomId == null) {
            return null;
        }
        return chatRoom.lastSendTime.before(findLastSendTimeById(chatRoomId));
    }

    private BooleanExpression equalsMemberId(Long memberId) {
        return chatRoom.sender.id.eq(memberId)
                .or(chatRoom.receiver.id.eq(memberId));
    }
}
