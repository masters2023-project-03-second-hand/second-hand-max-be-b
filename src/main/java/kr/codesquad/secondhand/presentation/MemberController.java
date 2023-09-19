package kr.codesquad.secondhand.presentation;

import java.util.Map;
import kr.codesquad.secondhand.application.member.MemberService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/api/members/{loginId}")
    public ApiResponse<Map<String, String>> modifyProfileImage(@RequestPart MultipartFile updateImageFile,
                                                               @Auth Long memberId) {
        String updatedProfileImageUrl = memberService.modifyProfileImage(updateImageFile, memberId);
        return new ApiResponse<>(HttpStatus.OK.value(), Map.of("profileImageUrl", updatedProfileImageUrl));
    }
}
