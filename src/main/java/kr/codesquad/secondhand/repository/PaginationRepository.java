package kr.codesquad.secondhand.repository;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public interface PaginationRepository {

    default <T> Slice<T> checkLastPage(int pageSize, List<T> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 다음 페이지 존재, next = true
        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.ofSize(pageSize), hasNext);
    }
}
