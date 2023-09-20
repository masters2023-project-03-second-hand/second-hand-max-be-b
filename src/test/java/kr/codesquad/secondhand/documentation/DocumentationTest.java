package kr.codesquad.secondhand.documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.codesquad.secondhand.presentation.AuthController;
import kr.codesquad.secondhand.presentation.CategoryController;
import kr.codesquad.secondhand.presentation.ChatController;
import kr.codesquad.secondhand.presentation.ItemController;
import kr.codesquad.secondhand.presentation.MemberController;
import kr.codesquad.secondhand.presentation.ResidenceController;
import kr.codesquad.secondhand.presentation.SalesHistoryController;
import kr.codesquad.secondhand.presentation.WishItemController;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest({
        AuthController.class,
        CategoryController.class,
        ChatController.class,
        ItemController.class,
        MemberController.class,
        ResidenceController.class,
        SalesHistoryController.class,
        WishItemController.class
})
@AutoConfigureRestDocs
public @interface DocumentationTest {
}
