package kr.codesquad.secondhand.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CustomSlice<T> {

    private final List<T> contents;
    private final Paging paging;
    @JsonInclude(Include.NON_NULL)
    private Long lastMessageId;

    public CustomSlice(List<T> contents, Long nextCursor, boolean hasNext) {
        this.contents = contents;
        this.paging = new Paging(nextCursor, hasNext);
    }

    public CustomSlice(List<T> contents, Long nextCursor, boolean hasNext, Long lastMessageId) {
        this(contents, nextCursor, hasNext);
        this.lastMessageId = lastMessageId;
    }

    @AllArgsConstructor
    @Getter
    public static class Paging {

        private final Long nextCursor;
        private final boolean hasNext;
    }
}
