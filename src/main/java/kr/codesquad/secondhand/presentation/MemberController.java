package kr.codesquad.secondhand.presentation;

import kr.codesquad.secondhand.application.MemberService;
import kr.codesquad.secondhand.presentation.dto.ApiResponse;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request,
                                                            @RequestParam String code, @RequestParam String state) {
        return new ResponseEntity(
                new ApiResponse<>(HttpStatus.OK.value(), memberService.login(request, code)),
                HttpStatus.OK
        );
    }
}
