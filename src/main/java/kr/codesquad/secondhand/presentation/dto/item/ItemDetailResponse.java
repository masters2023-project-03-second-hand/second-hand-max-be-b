package kr.codesquad.secondhand.presentation.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemDetailResponse {

    private final boolean isSeller;
    private final List<String> imageUrls;
    private final String seller;
    @JsonInclude(Include.NON_NULL)
    private final String status;
    private final String title;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final String content;
    private final int chatCount;
    private final int wishCount;
    private final int viewCount;
    private final Integer price;

    @Builder
    private ItemDetailResponse(boolean isSeller, List<String> imageUrls, String seller, String status, String title,
                               String categoryName, LocalDateTime createdAt, String content,
                               int chatCount, int wishCount, int viewCount, Integer price) {
        this.isSeller = isSeller;
        this.imageUrls = imageUrls;
        this.seller = seller;
        this.status = status;
        this.title = title;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.content = content;
        this.chatCount = chatCount;
        this.wishCount = wishCount;
        this.viewCount = viewCount;
        this.price = price;
    }

    public static ItemDetailResponse toSellerResponse(Item item, List<ItemImage> images) {
        return ItemDetailResponse.builder()
                .isSeller(true)
                .imageUrls(images.stream()
                        .map(ItemImage::getImageUrl)
                        .collect(Collectors.toUnmodifiableList()))
                .seller(item.getMember().getLoginId())
                .status(item.getStatus().getStatus())
                .title(item.getTitle())
                .categoryName(item.getCategoryName())
                .createdAt(item.getCreatedAt())
                .content(item.getContent())
                .chatCount(item.getChatCount())
                .wishCount(item.getWishCount())
                .viewCount(item.getViewCount())
                .price(item.getPrice())
                .build();
    }

    public static ItemDetailResponse toBuyerResponse(Item item, List<ItemImage> images) {
        return ItemDetailResponse.builder()
                .isSeller(false)
                .imageUrls(images.stream()
                        .map(ItemImage::getImageUrl)
                        .collect(Collectors.toUnmodifiableList()))
                .seller(item.getMember().getLoginId())
                .title(item.getTitle())
                .categoryName(item.getCategoryName())
                .createdAt(item.getCreatedAt())
                .content(item.getContent())
                .chatCount(item.getChatCount())
                .wishCount(item.getWishCount())
                .viewCount(item.getViewCount())
                .price(item.getPrice())
                .build();
    }
}
