package kr.codesquad.secondhand.domain.itemimage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import kr.codesquad.secondhand.domain.item.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_image")
@Entity
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512, nullable = false)
    private String imageUrl;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Builder
    private ItemImage(String imageUrl, Item item) {
        this.imageUrl = imageUrl;
        this.item = item;
    }

    public static ItemImage from(String imageUrl, Item item) {
        return ItemImage.builder()
                .imageUrl(imageUrl)
                .item(item)
                .build();
    }
}
