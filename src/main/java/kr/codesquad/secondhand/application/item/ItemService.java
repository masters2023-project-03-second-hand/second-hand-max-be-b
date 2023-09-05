package kr.codesquad.secondhand.application.item;

import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.ForbiddenException;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemDetailResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemStatusRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;
import kr.codesquad.secondhand.repository.category.CategoryRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import kr.codesquad.secondhand.repository.item.querydsl.ItemPaginationRepository;
import kr.codesquad.secondhand.repository.itemimage.ItemImageRepository;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import kr.codesquad.secondhand.repository.wishitem.WishItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {

    private final ImageService imageService;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ItemPaginationRepository itemPaginationRepository;
    private final WishItemRepository wishItemRepository;


    @Transactional
    public void register(List<MultipartFile> images, ItemRegisterRequest request, Long sellerId) {
        List<String> itemImageUrls = imageService.uploadImages(images);
        String thumbnailUrl = itemImageUrls.get(0);

        Member seller = memberRepository.getReferenceById(sellerId);

        Item savedItem = itemRepository.save(request.toEntity(seller, thumbnailUrl));

        List<ItemImage> itemImages = itemImageUrls.stream()
                .map(url -> ItemImage.from(url, savedItem))
                .collect(Collectors.toList());
        itemImageRepository.saveAllItemImages(itemImages);
    }

    public CustomSlice<ItemResponse> readAll(Long itemId, Long categoryId, String region, int pageSize) {
        String categoryName = null;
        if (categoryId != null) {
            categoryName = categoryRepository.findNameById(categoryId).orElse(null);
        }

        Slice<ItemResponse> response =
                itemPaginationRepository.findByIdAndCategoryName(itemId, categoryName, region, pageSize);
        List<ItemResponse> content = response.getContent();

        Long nextCursor = setNextCursor(content, response.hasNext());

        return new CustomSlice<>(content, nextCursor, response.hasNext());
    }

    private Long setNextCursor(List<ItemResponse> content, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext) {
            nextCursor = content.get(content.size() - 1).getItemId();
        }
        return nextCursor;
    }

    @Transactional
    public ItemDetailResponse read(Long memberId, Long itemId) {
        Item item = findItem(itemId);

        List<ItemImage> images = itemImageRepository.findByItemId(itemId);

        if (!item.isSeller(memberId)) {
            item.incrementViewCount();
            return ItemDetailResponse.toBuyerResponse(item, images);
        }
        return ItemDetailResponse.toSellerResponse(item, images);
    }

    @Transactional
    public void update(List<MultipartFile> images, ItemUpdateRequest request, Long itemId, Long sellerId) {
        Item item = findItem(itemId);
        if (!item.isSeller(sellerId)) {
            throw new ForbiddenException(ErrorCode.UNAUTHORIZED);
        }

        List<String> deleteImageUrls = request.getDeleteImageUrls();
        itemImageRepository.deleteByItem_IdAndImageUrlIn(itemId, deleteImageUrls);

        if (images != null) {
            saveImages(images, item);
        }

        if (item.isThumbnailDeleted(deleteImageUrls)) {
            String thumbnail = itemImageRepository.findByItemId(itemId).get(0).getImageUrl();
            item.changeThumbnail(thumbnail);
        }
        item.update(request);
    }

    @Transactional
    public void updateStatus(ItemStatusRequest request, Long itemId, Long sellerId) {
        Item item = findItem(itemId);
        if (!item.isSeller(sellerId)) {
            throw new ForbiddenException(ErrorCode.UNAUTHORIZED);
        }
        item.changeStatus(request.getStatus());
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    private void saveImages(List<MultipartFile> images, Item item) {
        List<String> itemImageUrls = imageService.uploadImages(images);
        List<ItemImage> itemImages = itemImageUrls.stream()
                .map(url -> ItemImage.from(url, item))
                .collect(Collectors.toList());
        itemImageRepository.saveAllItemImages(itemImages);
    }

    @Transactional
    public void delete(Long itemId, Long memberId) {
        Item item = findItem(itemId);
        if (!item.isSeller(memberId)) {
            throw new ForbiddenException(ErrorCode.UNAUTHORIZED);
        }
        List<ItemImage> imageUrls = itemImageRepository.findByItemId(itemId);
        imageService.deleteImages(imageUrls);

        itemImageRepository.deleteByItemId(itemId);
        wishItemRepository.deleteByItemId(itemId);
        itemRepository.deleteById(itemId);

        // todo: 삭제한 item과 관련된 채팅도 삭제하기
    }
}
