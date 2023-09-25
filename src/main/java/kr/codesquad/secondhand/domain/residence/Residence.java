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

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isSelected;

    @Builder
    private Residence(Long id, String addressName, Member member, Region region, boolean isSelected) {
        this.id = id;
        this.addressName = addressName;
        this.member = member;
        this.region = region;
        this.isSelected = isSelected;
    }

    public static Residence of(Long memberId, Long regionId, String addressName, boolean isSelected) {
        return Residence.builder()
                .addressName(addressName)
                .member(Member.builder()
                        .id(memberId)
                        .build())
                .region(Region.builder()
                        .id(regionId)
                        .build())
                .isSelected(isSelected)
                .build();
    }

    public void changeIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
