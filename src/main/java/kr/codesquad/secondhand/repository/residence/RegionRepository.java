package kr.codesquad.secondhand.repository.residence;

import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.domain.residence.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT region.addressName FROM Region region WHERE region.id IN (:regionIds)")
    List<String> findAddressNamesByIds(@Param("regionIds") List<Long> regionIds);

    Optional<Region> findByAddressName(String addressName);
}
