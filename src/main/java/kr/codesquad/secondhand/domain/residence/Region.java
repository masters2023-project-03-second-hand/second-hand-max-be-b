package kr.codesquad.secondhand.domain.residence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String fullAddressName;

    @Column(nullable = false, length = 16)
    private String addressName;

    @Builder
    private Region(Long id, String fullAddressName, String addressName) {
        this.id = id;
        this.fullAddressName = fullAddressName;
        this.addressName = addressName;
    }
}
