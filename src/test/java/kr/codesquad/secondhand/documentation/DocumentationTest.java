package kr.codesquad.secondhand.documentation;

import kr.codesquad.secondhand.presentation.*;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
        WishItemController.class,
        FcmController.class
})
@AutoConfigureRestDocs
public @interface DocumentationTest {
}
