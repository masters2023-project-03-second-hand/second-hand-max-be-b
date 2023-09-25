package kr.codesquad.secondhand.repository.residence;

import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.domain.residence.Residence;
import kr.codesquad.secondhand.repository.residence.querydsl.ResidenceRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenceRepository extends JpaRepository<Residence, Long>, ResidenceRepositoryCustom {

    int countByMemberId(Long memberId);

    void deleteByAddressName(String addressName);

    List<Residence> findResidenceByMember_Id(Long memberId);

    Optional<Residence> findByMember_Id(Long memberId);
}
