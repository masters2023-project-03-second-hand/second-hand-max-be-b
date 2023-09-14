package kr.codesquad.secondhand.repository.residence;

import kr.codesquad.secondhand.domain.residence.Residence;
import kr.codesquad.secondhand.repository.residence.querydsl.ResidenceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenceRepository extends JpaRepository<Residence, Long>, ResidenceRepositoryCustom {

    int countByMemberId(Long memberId);

    void deleteByAddressName(String addressName);
}
