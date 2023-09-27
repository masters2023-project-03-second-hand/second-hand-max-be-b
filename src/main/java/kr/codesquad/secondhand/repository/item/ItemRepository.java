package kr.codesquad.secondhand.repository.item;

import java.util.Optional;
import javax.persistence.LockModeType;
import kr.codesquad.secondhand.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT item FROM Item item WHERE item.id = :itemId")
    Optional<Item> findByIdWithPessimisticLock(@Param("itemId") Long itemId);
}
