package kr.codesquad.secondhand.repository.member;

import kr.codesquad.secondhand.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
