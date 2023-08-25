package kr.codesquad.secondhand.application;

import kr.codesquad.secondhand.DatabaseInitializerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(DatabaseInitializerExtension.class)
@SpringBootTest
public @interface ApplicationTest {
}
