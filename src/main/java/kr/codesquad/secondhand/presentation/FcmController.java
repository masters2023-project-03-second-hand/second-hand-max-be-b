package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.firebase.FcmTokenService;
import kr.codesquad.secondhand.presentation.dto.fcm.FcmTokenIssueResponse;
import kr.codesquad.secondhand.presentation.dto.fcm.FcmTokenUpdateRequest;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api/fcm-token")
@RestController
public class FcmController {

    private final FcmTokenService fcmTokenService;

    @PatchMapping
    public void updateToken(@Valid @RequestBody FcmTokenUpdateRequest request,
                            @Auth Long memberId) {
        fcmTokenService.updateToken(request.getToken(), memberId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FcmTokenIssueResponse issueToken() {
        return fcmTokenService.issueToken();
    }
}
