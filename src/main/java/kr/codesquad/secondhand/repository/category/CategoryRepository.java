package kr.codesquad.secondhand.repository.category;

import kr.codesquad.secondhand.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
