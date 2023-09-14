package kr.codesquad.secondhand.repository.residence.querydsl;

import static kr.codesquad.secondhand.domain.residence.QRegion.region;
import static kr.codesquad.secondhand.domain.residence.QResidence.residence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResidenceRepositoryImpl implements ResidenceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AddressData> findByMemberId(Long memberId) {
        return queryFactory
                .select(Projections.fields(AddressData.class,
                        region.id.as("addressId"),
                        region.fullAddressName,
                        region.addressName))
                .from(residence)
                .innerJoin(residence.region, region)
                .where(residence.member.id.eq(memberId))
                .fetch();
    }
}
