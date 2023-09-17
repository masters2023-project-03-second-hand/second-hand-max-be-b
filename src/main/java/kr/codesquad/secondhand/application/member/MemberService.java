package kr.codesquad.secondhand.application.member;

import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Value("${custom.default-profile}")
    private String defaultProfileImage;

    @Transactional
    public void modifyProfileImage(MultipartFile profileImage, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));

        if (profileImage == null || profileImage.isEmpty()) {
            member.changeProfileImage(defaultProfileImage);
            return;
        }

        String updatedProfileImageUrl = imageService.uploadImage(profileImage);
        member.changeProfileImage(updatedProfileImageUrl);
    }
}
