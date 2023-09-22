package kr.codesquad.secondhand.repository.chat.querydsl;

import static kr.codesquad.secondhand.domain.chat.QChatRoom.chatRoom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatPaginationRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<ChatRoomResponse> findByMemberId(Long memberId, Pageable pageable) {
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
                .where(equalsMemberId(memberId))
                .orderBy(chatRoom.lastSendTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Integer hasNext = queryFactory
                .selectOne()
                .from(chatRoom)
                .where(equalsMemberId(memberId),
                        chatRoom.lastSendTime.before(
                                chatRoomResponses.get(chatRoomResponses.size() - 1).getLastSendTime()))
                .fetchOne();

        return new SliceImpl<>(chatRoomResponses, PageRequest.ofSize(pageable.getPageSize()), hasNext != null);
    }

    private Expression<String> createPartnerNameExpression(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.buyer.id.eq(memberId))
                .then(chatRoom.seller.loginId)
                .otherwise(chatRoom.buyer.loginId)
                .as("chatPartnerName");
    }

    private Expression<String> createPartnerProfileExpression(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.buyer.id.eq(memberId))
                .then(chatRoom.seller.profileUrl)
                .otherwise(chatRoom.buyer.profileUrl)
                .as("chatPartnerProfile");
    }

    private BooleanExpression equalsMemberId(Long memberId) {
        return chatRoom.buyer.id.eq(memberId)
                .or(chatRoom.seller.id.eq(memberId));
    }
}
