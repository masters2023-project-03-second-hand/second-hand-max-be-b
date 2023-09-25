package kr.codesquad.secondhand.application.residence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.residence.Region;
import kr.codesquad.secondhand.domain.residence.Residence;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import kr.codesquad.secondhand.repository.residence.RegionRepository;
import kr.codesquad.secondhand.repository.residence.ResidenceRepository;
import kr.codesquad.secondhand.repository.residence.querydsl.RegionPaginationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ResidenceService {

    private static final int MEMBER_HAS_RESIDENCE_MIN_COUNT = 1;
    private static final int MEMBER_HAS_RESIDENCE_MAX_COUNT = 2;
    private static final Function<List<Residence>, Residence> selectMainResidence = (residences -> residences.get(0));

    private final RegionPaginationRepository regionPaginationRepository;
    private final RegionRepository regionRepository;
    private final ResidenceRepository residenceRepository;

    @Transactional
    public void saveResidence(List<Long> regionIds, Member member) {
        List<String> addressNames = regionRepository.findAddressNamesByIds(regionIds);

        List<Residence> residences = new ArrayList<>();
        for (int i = 0; i < addressNames.size(); i++) {
            residences.add(Residence.builder()
                    .addressName(addressNames.get(i))
                    .member(member)
                    .region(Region.builder()
                            .id(regionIds.get(i))
                            .build())
                    .build());
        }
        selectMainResidence.apply(residences).selectToMainResidence();
        residenceRepository.saveAll(residences);
    }

    public CustomSlice<RegionResponse> readAllRegion(Long cursor, int size, String region) {
        Slice<RegionResponse> regionResponses = regionPaginationRepository.findByRegionName(cursor, region, size);
        boolean hasNext = regionResponses.hasNext();

        List<RegionResponse> response = regionResponses.getContent();
        return new CustomSlice<>(response, setNextCursor(response, hasNext), hasNext);
    }

    private Long setNextCursor(List<RegionResponse> content, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext) {
            nextCursor = content.get(content.size() - 1).getAddressId();
        }
        return nextCursor;
    }

    @Transactional
    public void register(Long addressId, Long memberId) {
        if (residenceRepository.countByMemberId(memberId) >= MEMBER_HAS_RESIDENCE_MAX_COUNT) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "사용자의 거주 지역은 최대 두 개까지 설정 가능합니다.");
        }

        Region region = regionRepository.findById(addressId)
                .orElseThrow(() -> NotFoundException.regionNotFound(ErrorCode.NOT_FOUND, addressId));

        residenceRepository.save(Residence.of(memberId, region.getId(), region.getAddressName(), false));
    }

    @Transactional
    public void remove(Long addressId, Long memberId) {
        if (residenceRepository.countByMemberId(memberId) <= MEMBER_HAS_RESIDENCE_MIN_COUNT) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "사용자의 거주 지역은 최소 한 개는 있어야 합니다.");
        }
        Region region = regionRepository.findById(addressId)
                .orElseThrow(() -> NotFoundException.regionNotFound(ErrorCode.NOT_FOUND, addressId));

        residenceRepository.deleteByAddressName(region.getAddressName());

        Long remainAddressId = residenceRepository.findByMemberId(memberId).get(0).getAddressId();
        Residence residence = residenceRepository.findById(remainAddressId).get();
        residence.selectToMainResidence();
    }

    public List<AddressData> readResidenceOfMember(Long memberId) {
        return residenceRepository.findByMemberId(memberId);
    }

    @Transactional
    public void selectResidence(Long regionId, Long memberId) {
        List<Residence> residences = residenceRepository.findResidenceByMember_Id(memberId);
        residences.forEach(residence -> residence.changeIsSelected(residence.getRegion().getId().equals(regionId)));
    }
}
