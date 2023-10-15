package kr.codesquad.secondhand.repository.member;

import java.util.Optional;
import kr.codesquad.secondhand.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
