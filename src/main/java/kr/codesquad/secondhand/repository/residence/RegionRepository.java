package kr.codesquad.secondhand.repository.residence;

import java.util.List;
import kr.codesquad.secondhand.domain.residence.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT region.id FROM Region region WHERE region.addressName IN (:addressNames)")
    List<Long> findAllIdsById(@Param("addressNames") List<String> addressNames);
}
