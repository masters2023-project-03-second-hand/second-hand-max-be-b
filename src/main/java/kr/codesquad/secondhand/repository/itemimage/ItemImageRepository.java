package kr.codesquad.secondhand.repository.itemimage;

import java.util.List;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, ItemImageRepositoryCustom {

    List<ItemImage> findByItemId(Long itemId);
}
