package kr.codesquad.secondhand.repository.wishitem.querydsl;

import static kr.codesquad.secondhand.domain.item.QItem.item;
import static kr.codesquad.secondhand.domain.wishitem.QWishItem.wishItem;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WishItemCategoryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findCategoryNameByMemberId(Long memberId) {
        return queryFactory
                .selectDistinct(item.categoryName)
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .where(equalMemberId(memberId))
                .orderBy(item.categoryName.asc())
                .fetch();
    }

    private BooleanExpression equalMemberId(Long memberId) {
        return wishItem.member.id.eq(memberId);
    }
}
