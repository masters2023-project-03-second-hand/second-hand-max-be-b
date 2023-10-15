package kr.codesquad.secondhand.repository.category;

import java.util.Optional;
import kr.codesquad.secondhand.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT category.name FROM Category category WHERE category.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    @Query("SELECT category.id FROM Category category WHERE category.name = :name")
    Long findIdByName(@Param("name") String name);
}
