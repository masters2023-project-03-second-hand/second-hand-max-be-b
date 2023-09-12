package kr.codesquad.secondhand.domain.residence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kr.codesquad.secondhand.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "residence")
@Entity
public class Residence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String addressName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Builder
    private Residence(Long id, String addressName, Member member, Region region) {
        this.id = id;
        this.addressName = addressName;
        this.member = member;
        this.region = region;
    }

    public static Residence from(Long memberId, Long regionId, String addressName) {
        return Residence.builder()
                .addressName(addressName)
                .member(Member.builder()
                        .id(memberId)
                        .build())
                .region(Region.builder()
                        .id(regionId)
                        .build())
                .build();
    }
}
