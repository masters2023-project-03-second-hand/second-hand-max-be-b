package kr.codesquad.secondhand.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CustomSlice<T> {

    private final List<T> contents;
    private final Paging paging;

    public CustomSlice(List<T> contents, Long nextCursor, boolean hasNext) {
        this.contents = contents;
        this.paging = new Paging(nextCursor, hasNext);
    }

    @AllArgsConstructor
    @Getter
    public static class Paging {

        private final Long nextCursor;
        private final boolean hasNext;
    }
}
