package kr.codesquad.secondhand.repository.residence;

import kr.codesquad.secondhand.domain.residence.Residence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {

    int countByMemberId(Long memberId);
}
