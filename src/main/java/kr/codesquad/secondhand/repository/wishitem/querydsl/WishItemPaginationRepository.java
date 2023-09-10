package kr.codesquad.secondhand.repository.wishitem.querydsl;

import static kr.codesquad.secondhand.domain.item.QItem.item;
import static kr.codesquad.secondhand.domain.wishitem.QWishItem.wishItem;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.repository.PaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WishItemPaginationRepository implements PaginationRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<ItemResponse> findAll(Long memberId, Long wishItemId, String categoryName, int pageSize) {
        List<ItemResponse> itemResponses = queryFactory.select(Projections.fields(ItemResponse.class,
                        wishItem.item.id.as("itemId"),
                        item.thumbnailUrl,
                        item.title,
                        item.tradingRegion,
                        item.createdAt,
                        item.price,
                        item.status,
                        item.chatCount,
                        item.wishCount))
                .from(wishItem)
                .innerJoin(wishItem.item, item).on(wishItem.item.id.eq(item.id))
                .where(lessThanId(wishItemId),
                        eqMemberId(memberId),
                        eqCategoryName(categoryName))
                .orderBy(wishItem.createdAt.desc())
                .limit(pageSize + 1)    // 다음 요소가 있는지 확인하기 위해 +1개 만큼 더 가져온다.
                .fetch();
        return checkLastPage(pageSize, itemResponses);
    }

    private BooleanExpression lessThanId(Long wishItemId) {
        if (wishItemId == null) {
            return null;
        }
        return wishItem.id.lt(wishItemId);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        if (memberId == null) {
            return null;
        }
        return wishItem.member.id.eq(memberId);
    }

    private BooleanExpression eqCategoryName(String categoryName) {
        if (categoryName == null) {
            return null;
        }
        return item.categoryName.eq(categoryName);
    }
}
