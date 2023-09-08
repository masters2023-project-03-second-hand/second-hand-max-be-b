package kr.codesquad.secondhand.repository.residence.querydsl;

import static kr.codesquad.secondhand.domain.residence.QRegion.region;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import kr.codesquad.secondhand.repository.PaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RegionPaginationRepository implements PaginationRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<RegionResponse> findByRegionName(Long regionId, String regionName, int pageSize) {
        List<RegionResponse> regionResponses = queryFactory
                .select(Projections.fields(RegionResponse.class,
                        region.id.as("addressId"),
                        region.fullAddressName,
                        region.addressName))
                .from(region)
                .where(greaterThanRegionId(regionId),
                        likeRegionName(regionName)
                )
                .orderBy(region.id.asc())
                .limit(pageSize + 1)    // 다음 요소가 있는지 확인하기 위해 +1개 만큼 더 가져온다.
                .fetch();

        return checkLastPage(pageSize, regionResponses);
    }

    private BooleanExpression greaterThanRegionId(Long regionId) {
        if (regionId == null) {
            return null;
        }

        return region.id.gt(regionId);
    }

    private BooleanExpression likeRegionName(String regionName) {
        if (regionName == null) {
            return null;
        }

        return region.addressName.like(regionName + "%");
    }
}
