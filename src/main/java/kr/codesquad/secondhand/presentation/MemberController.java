package kr.codesquad.secondhand.presentation;

import javax.validation.Valid;
import kr.codesquad.secondhand.application.auth.MemberService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.token.AccessTokenResponse;
import kr.codesquad.secondhand.presentation.dto.token.TokenRenewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request,
                                                            @RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), memberService.login(request, code)));
    }

    @PostMapping("/signup/naver")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request,
                                                    @RequestParam String code, @RequestParam String state) {
        memberService.signUp(request, code);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value()));
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> renewAccessToken(@RequestBody TokenRenewRequest request) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(
                        HttpStatus.OK.value(),
                        tokenService.renewAccessToken(request.getRefreshToken())
                ));
    }
}
