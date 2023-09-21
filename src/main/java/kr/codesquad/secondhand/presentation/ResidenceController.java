package kr.codesquad.secondhand.presentation;

import javax.validation.Valid;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import kr.codesquad.secondhand.presentation.dto.residence.ResidenceRequest;
import kr.codesquad.secondhand.presentation.dto.residence.ResidenceSelectRequest;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ApiResponse<Void> registerResidence(@Valid @RequestBody ResidenceRequest request,
                                               @Auth Long memberId) {
        residenceService.register(request.getAddressId(), memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @DeleteMapping
    public ApiResponse<Void> removeResidence(@Valid @RequestBody ResidenceRequest request,
                                             @Auth Long memberId) {
        residenceService.remove(request.getAddressId(), memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }

    @PutMapping
    public ApiResponse<Void> selectResidence(@Valid @RequestBody ResidenceSelectRequest request,
                                             @Auth Long memberId) {
        residenceService.selectResidence(request.getSelectedAddressId(), memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }
}
