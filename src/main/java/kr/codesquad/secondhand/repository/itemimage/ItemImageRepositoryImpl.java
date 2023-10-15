package kr.codesquad.secondhand.repository.itemimage;

import java.util.List;
import java.util.stream.Collectors;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@RequiredArgsConstructor
public class ItemImageRepositoryImpl implements ItemImageRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void saveAllItemImages(List<ItemImage> itemImages) {
        // IDENTITY 방식의 한계로 bulk insert query 직접 구현
        String sql = "INSERT INTO item_image "
                + "(image_url, item_id) VALUES (:imageUrl, :itemId)";
        MapSqlParameterSource[] params = itemImages.stream()
                .map(itemImage -> new MapSqlParameterSource()
                        .addValue("imageUrl", itemImage.getImageUrl())
                        .addValue("itemId", itemImage.getItem().getId()))
                .collect(Collectors.toList())
                .toArray(MapSqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, params);
    }
}
