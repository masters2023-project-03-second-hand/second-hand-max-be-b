package kr.codesquad.secondhand.domain.item;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kr.codesquad.secondhand.domain.AuditingFields;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "item")
@Entity
public class Item extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column
    private Integer price;

    @Column(length = 100, nullable = false)
    private String tradingRegion;

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private Integer viewCount;

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private Integer wishCount;

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private Integer chatCount;

    @Column(length = 45, nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(length = 45, nullable = false)
    private String categoryName;

    @Column(length = 512, nullable = false)
    private String thumbnailUrl;

    @JoinColumn(name = "seller_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    private Item(String title, String content, Integer price, String tradingRegion,
                 ItemStatus status, String categoryName, String thumbnailUrl, Member member) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.tradingRegion = tradingRegion;
        this.status = status;
        this.categoryName = categoryName;
        this.thumbnailUrl = thumbnailUrl;
        this.member = member;
    }

    public static Item toEntity(ItemRegisterRequest request, Member member, String thumbnailUrl) {
        return Item.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .tradingRegion(request.getRegion())
                .status(ItemStatus.of(request.getStatus()))
                .categoryName(request.getCategoryName())
                .thumbnailUrl(thumbnailUrl)
                .member(member)
                .build();
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    public boolean isSeller(Long memberId) {
        return this.member.getId() == memberId;
    }

    public void update(ItemUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.price = request.getPrice();
        this.tradingRegion = request.getRegion();
        this.status = ItemStatus.of(request.getStatus());
        this.categoryName = request.getCategoryName();
    }

    public boolean isThumbnailDeleted(List<String> deleteImageUrls) {
        return deleteImageUrls.stream()
                .anyMatch(deleteImage -> thumbnailUrl.equals(deleteImage));
    }

    public void changeThumbnail(String imageUrl) {
        this.thumbnailUrl = imageUrl;
    }

    public void changeStatus(String status) {
        this.status = ItemStatus.of(status);
    }
}
