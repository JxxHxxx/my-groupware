package com.jxx.vacation.core.common.pagination;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageService {

    private final int page;
    private final int size;

    public PageService(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public <T> PageImpl<T> convertToPage(List<T> responses) {
        int total = responses.size();
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        return new PageImpl<>(responses.subList(start, end), pageable, total);
    }
}
