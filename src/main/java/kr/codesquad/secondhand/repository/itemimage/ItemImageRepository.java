package kr.codesquad.secondhand.repository.itemimage;

import java.util.List;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, ItemImageRepositoryCustom {

    List<ItemImage> findByItemId(Long itemId);

    void deleteByItem_IdAndImageUrlIn(Long itemId, List<String> deleteImageUrls);

    @Modifying
    @Query("DELETE FROM ItemImage itemImage WHERE itemImage.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);
}
