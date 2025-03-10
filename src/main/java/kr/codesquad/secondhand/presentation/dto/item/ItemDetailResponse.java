package kr.codesquad.secondhand.presentation.dto.item;

import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemDetailResponse {

    private final Boolean isSeller;
    private final List<String> imageUrls;
    private final String seller;
    private final String status;
    private final String title;
    private final String categoryName;
    private final LocalDateTime createdAt;
    private final String content;
    private final int chatCount;
    private final int wishCount;
    private final int viewCount;
    private final Long price;
    private final Boolean isInWishList;
    private final Long chatRoomId;

    @Builder
    public ItemDetailResponse(Boolean isSeller, List<String> imageUrls, String seller, String status, String title,
                              String categoryName, LocalDateTime createdAt, String content, int chatCount,
                              int wishCount,
                              int viewCount, Long price, Boolean isInWishList, Long chatRoomId) {
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
        this.isInWishList = isInWishList;
        this.chatRoomId = chatRoomId;
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

    public static ItemDetailResponse toBuyerResponse(Item item, List<ItemImage> images, Boolean isInWishList, Long chatRoomId) {
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
                .isInWishList(isInWishList)
                .chatRoomId(chatRoomId)
                .build();
    }
}
