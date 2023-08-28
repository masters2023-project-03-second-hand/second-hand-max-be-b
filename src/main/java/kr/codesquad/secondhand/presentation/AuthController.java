package kr.codesquad.secondhand.presentation;

import java.util.Optional;
import javax.validation.Valid;
import kr.codesquad.secondhand.application.auth.AuthService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
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
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login/naver")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request,
                                                            @RequestParam Optional<String> code,
                                                            @RequestParam Optional<String> state) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(
                        HttpStatus.OK.value(),
                        authService.login(
                                request,
                                code.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER))
                        )
                ));
    }

    @PostMapping("/signup/naver")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request,
                                                    @RequestParam Optional<String> code,
                                                    @RequestParam Optional<String> state) {
        authService.signUp(request, code.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER)));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value()));
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> renewAccessToken(
            @Valid @RequestBody TokenRenewRequest request) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(
                        HttpStatus.OK.value(),
                        tokenService.renewAccessToken(request.getRefreshToken())
                ));
    }
}
