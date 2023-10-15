package kr.codesquad.secondhand.application.category;

import java.util.List;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import kr.codesquad.secondhand.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryListResponse read() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryListResponse.toResponse(categories);
    }
}
