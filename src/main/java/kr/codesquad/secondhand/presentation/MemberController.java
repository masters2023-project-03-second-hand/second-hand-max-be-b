package kr.codesquad.secondhand.presentation;

import java.util.List;
import kr.codesquad.secondhand.application.member.MemberService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import kr.codesquad.secondhand.presentation.dto.member.ModifyProfileResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
    public ApiResponse<List<AddressData>> readResidences(@Auth Long memberId) {
        return new ApiResponse<>(HttpStatus.OK.value(), residenceService.readResidenceOfMember(memberId));
    }
}
