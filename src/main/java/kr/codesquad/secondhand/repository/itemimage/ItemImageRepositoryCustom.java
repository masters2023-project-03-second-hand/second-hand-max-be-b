package kr.codesquad.secondhand.repository.itemimage;

import java.util.List;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;

public interface ItemImageRepositoryCustom {

    void saveAllItemImages(List<ItemImage> itemImages);
}
