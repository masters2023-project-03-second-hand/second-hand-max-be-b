package kr.codesquad.secondhand.documentation.support;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

import java.util.stream.Collectors;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ConstraintsHelper {

    private static ConstraintDescriptions simpleRequestConstraints;

    public static <T> FieldDescriptor withPath(String path, Class<T> classType) {
        simpleRequestConstraints = new ConstraintDescriptions(classType);

        return fieldWithPath(path)
                .attributes(
                        key("constraints").value(simpleRequestConstraints.descriptionsForProperty(path)
                                .stream()
                                .collect(Collectors.joining("\n\n"))));
    }
}
