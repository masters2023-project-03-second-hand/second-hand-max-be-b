package kr.codesquad.secondhand.presentation;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import kr.codesquad.secondhand.application.auth.AuthService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.member.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.member.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.member.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.token.AccessTokenResponse;
import kr.codesquad.secondhand.presentation.dto.token.TokenRenewRequest;
import kr.codesquad.secondhand.presentation.support.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/naver/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request,
                                            @RequestParam Optional<String> code,
                                            @RequestParam Optional<String> state) {
        return new ApiResponse<>(HttpStatus.OK.value(),
                authService.login(request,
                        code.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER))));
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/naver/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> signUp(@RequestPart @Valid SignUpRequest request,
                                    @RequestParam Optional<String> code,
                                    @RequestParam Optional<String> state,
                                    @RequestPart Optional<MultipartFile> profile) {
        authService.signUp(request,
                code.orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PARAMETER)),
                profile);
        return new ApiResponse<>(HttpStatus.CREATED.value());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/token")
    public ApiResponse<AccessTokenResponse> renewAccessToken(@Valid @RequestBody TokenRenewRequest request) {
        return new ApiResponse<>(HttpStatus.OK.value(), tokenService.renewAccessToken(request.getRefreshToken()));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, @Auth Long memberId) {
        authService.logout(request, memberId);
        return new ApiResponse<>(HttpStatus.OK.value());
    }
}
