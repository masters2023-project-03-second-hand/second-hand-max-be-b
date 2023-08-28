package kr.codesquad.secondhand.repository.item;

import kr.codesquad.secondhand.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
