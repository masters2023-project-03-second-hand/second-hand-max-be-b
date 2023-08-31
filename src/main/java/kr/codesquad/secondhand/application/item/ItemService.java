package kr.codesquad.secondhand.application.item;

import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemDetailResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.repository.category.CategoryRepository;
import kr.codesquad.secondhand.repository.item.ItemRepository;
import kr.codesquad.secondhand.repository.item.querydsl.ItemPaginationRepository;
import kr.codesquad.secondhand.repository.itemimage.ItemImageRepository;
import kr.codesquad.secondhand.repository.member.MemberRepository;
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

    @Transactional
    public void register(List<MultipartFile> images, ItemRegisterRequest request, Long sellerId) {
        List<String> itemImageUrls = imageService.uploadImages(images);
        String thumbnailUrl = itemImageUrls.get(0);

        Member seller = memberRepository.getReferenceById(sellerId);

        Item savedItem = itemRepository.save(Item.toEntity(request, seller, thumbnailUrl));

        List<ItemImage> itemImages = itemImageUrls.stream()
                .map(url -> ItemImage.toEntity(url, savedItem))
                .collect(Collectors.toList());
        itemImageRepository.saveAllItemImages(itemImages);
    }

    public CustomSlice<ItemResponse> readAll(Long itemId, Long categoryId, int pageSize) {
        String categoryName = null;
        if (categoryId != null) {
            categoryName = categoryRepository.findNameById(categoryId).orElse(null);
        }

        Slice<ItemResponse> response = itemPaginationRepository.findByIdAndCategoryName(itemId, categoryName, pageSize);
        List<ItemResponse> content = response.getContent();

        Long nextCursor = setNextCursor(content, pageSize);

        return new CustomSlice<>(content, nextCursor, response.hasNext());
    }

    private Long setNextCursor(List<ItemResponse> content, int pageSize) {
        Long nextCursor = null;
        if (content.size() == pageSize) {
            nextCursor = content.get(content.size() - 1).getItemId();
        }
        return nextCursor;
    }


    @Transactional
    public ItemDetailResponse read(Long memberId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "상품을 찾을 수 없습니다."));

        List<ItemImage> images = itemImageRepository.findByItemId(itemId);

        if (!item.isSeller(memberId)) {
            item.incrementViewCount();
            return ItemDetailResponse.toBuyerResponse(item, images);
        }
        return ItemDetailResponse.toSellerResponse(item, images);
    }
}
