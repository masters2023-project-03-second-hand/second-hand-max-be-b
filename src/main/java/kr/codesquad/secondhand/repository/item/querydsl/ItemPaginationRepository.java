package kr.codesquad.secondhand.repository.item.querydsl;

import static kr.codesquad.secondhand.domain.item.QItem.item;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.repository.PaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ItemPaginationRepository implements PaginationRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<ItemResponse> findByIdAndCategoryName(Long itemId, String categoryName, String region, int pageSize) {
        List<ItemResponse> itemResponses = queryFactory
                .select(Projections.fields(ItemResponse.class,
                        item.id.as("itemId"),
                        item.thumbnailUrl,
                        item.title,
                        item.tradingRegion,
                        item.createdAt,
                        item.price,
                        item.status,
                        item.member.loginId.as("sellerId"),
                        item.chatCount,
                        item.wishCount))
                .from(item)
                .where(lessThanItemId(itemId),
                        equalCategoryName(categoryName),
                        equalTradingRegion(region)
                )
                .orderBy(item.createdAt.desc())
                .limit(pageSize + 1)    // 다음 요소가 있는지 확인하기 위해 +1개 만큼 더 가져온다.
                .fetch();
        return checkLastPage(pageSize, itemResponses);
    }

    public Slice<ItemResponse> findByIdAndStatus(Long itemId, ItemStatus status, int pageSize, Long memberId) {
        List<ItemResponse> itemResponses = queryFactory
                .select(Projections.fields(ItemResponse.class,
                        item.id.as("itemId"),
                        item.thumbnailUrl,
                        item.title,
                        item.tradingRegion,
                        item.createdAt,
                        item.price,
                        item.status,
                        item.member.loginId.as("sellerId"),
                        item.chatCount,
                        item.wishCount))
                .from(item)
                .where(lessThanItemId(itemId),
                        equalStatus(status),
                        equalMemberId(memberId)
                )
                .orderBy(item.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();
        return checkLastPage(pageSize, itemResponses);
    }

    private BooleanExpression lessThanItemId(Long itemId) {
        if (itemId == null) {
            return null;
        }

        return item.id.lt(itemId);
    }

    private BooleanExpression equalCategoryName(String categoryName) {
        if (categoryName == null) {
            return null;
        }

        return item.categoryName.eq(categoryName);
    }

    private BooleanExpression equalTradingRegion(String region) {
        if (region == null) {
            return null;
        }

        return item.tradingRegion.like(region + "%");
    }

    private BooleanExpression equalStatus(ItemStatus status) {
        if (status == null) {
            return null;
        }
        ItemStatus soldOut = ItemStatus.SOLD_OUT;

        if (status.getStatus().equals(soldOut.getStatus())) {
            return item.status.eq(status);
        }
        return item.status.ne(soldOut);
    }

    private BooleanExpression equalMemberId(Long memberId) {
        return item.member.id.eq(memberId);
    }
}
