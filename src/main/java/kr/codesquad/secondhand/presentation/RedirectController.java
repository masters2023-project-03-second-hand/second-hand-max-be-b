package kr.codesquad.secondhand.presentation;

import javax.validation.Valid;
import kr.codesquad.secondhand.presentation.dto.category.CategoryRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

    @PostMapping("/api/categories")
    public RedirectView redirect(@Valid @RequestBody CategoryRequest request) {
        RedirectView redirectView = new RedirectView();
        String redirectUrl = "/api/items?categoryId=" + request.getSelectedCategoryId();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }
}
