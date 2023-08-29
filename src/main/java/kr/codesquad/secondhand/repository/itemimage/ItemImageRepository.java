package kr.codesquad.secondhand.repository.itemimage;

import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long>, ItemImageRepositoryCustom {
}
