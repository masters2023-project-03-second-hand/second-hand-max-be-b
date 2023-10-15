package kr.codesquad.secondhand.acceptance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.codesquad.secondhand.DatabaseInitializerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(DatabaseInitializerExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public @interface AcceptanceTest {
}
