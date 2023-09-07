package kr.codesquad.secondhand.application.residence;

import java.util.ArrayList;
import java.util.List;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.residence.Region;
import kr.codesquad.secondhand.domain.residence.Residence;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.member.SignUpRequest;
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

    private final RegionPaginationRepository regionPaginationRepository;
    private final RegionRepository regionRepository;
    private final ResidenceRepository residenceRepository;

    @Transactional
    public void saveResidence(SignUpRequest request, Member member) {
        List<Long> regionIds = regionRepository.findAllIdsById(request.getAddressNames());
        List<String> addressNames = request.getAddressNames();

        List<Residence> residences = new ArrayList<>();
        for (int i = 0; i < regionIds.size(); i++) {
            residences.add(Residence.builder()
                    .addressName(addressNames.get(i))
                    .member(member)
                    .region(Region.builder()
                            .id(regionIds.get(i))
                            .build())
                    .build());
        }
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
}
