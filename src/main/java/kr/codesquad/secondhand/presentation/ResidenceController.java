package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/regions")
@RestController
public class ResidenceController {

    private final ResidenceService residenceService;

    @GetMapping
    public ApiResponse<CustomSlice<RegionResponse>> readAllRegions(@RequestParam(required = false) Long cursor,
                                                                   @RequestParam(required = false, defaultValue = "10") int size,
                                                                   @RequestParam(required = false) String region) {
        return new ApiResponse<>(HttpStatus.OK.value(), residenceService.readAllRegion(cursor, size, region));
    }
}
