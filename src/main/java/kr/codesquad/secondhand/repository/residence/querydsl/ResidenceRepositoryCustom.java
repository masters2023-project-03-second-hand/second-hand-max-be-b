package kr.codesquad.secondhand.repository.residence.querydsl;

import java.util.List;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;

public interface ResidenceRepositoryCustom {

    List<AddressData> findByMemberId(Long memberId);
}
