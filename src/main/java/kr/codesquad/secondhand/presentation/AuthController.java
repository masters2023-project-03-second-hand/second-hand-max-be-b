package kr.codesquad.secondhand.presentation;

import java.util.Optional;
import javax.validation.Valid;
import kr.codesquad.secondhand.application.auth.AuthService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.application.image.ImageService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/naver/login")
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

    @PostMapping("/naver/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid SignUpRequest request,
                                                    @RequestParam Optional<String> code,
                                                    @RequestParam Optional<String> state,
                                                    @RequestPart Optional<MultipartFile> profile) {
        authService.signUp(request,
                code.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER)),
                profile);
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
