package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.member.MemberService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.member.MemberResidencesResponse;
import kr.codesquad.secondhand.presentation.dto.member.ModifyProfileResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final ResidenceService residenceService;

    @PutMapping("/{loginId}")
    public ApiResponse<ModifyProfileResponse> modifyProfileImage(@RequestPart MultipartFile updateImageFile,
                                                                 @Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(), memberService.modifyProfileImage(updateImageFile, memberId));
    }

    @GetMapping("/regions")
    public ApiResponse<MemberResidencesResponse> readResidences(@Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(),
                new MemberResidencesResponse(residenceService.readResidenceOfMember(memberId)));
    }
}
