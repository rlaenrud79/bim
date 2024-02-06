package com.devo.bim.model.dto;

import com.querydsl.core.QueryResults;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@NoArgsConstructor
public class PageDTO<T> {

    private Page<T> pageList;
    private long totalCount;
    private long successCount;
    private long failCount;
    private long noneCount;

    public PageDTO(List<T> content, Pageable pageable, long totalCount){
        this.pageList = new PageImpl<>(content, pageable, totalCount);
        this.totalCount = totalCount;
    }

    public PageDTO(List<T> content, Pageable pageable, long totalCount, long successCount, long failCount, long noneCount){
        this.pageList = new PageImpl<>(content, pageable, totalCount);
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.noneCount = noneCount;
    }

    public PageDTO(QueryResults<T> results, Pageable pageable){
        this.pageList = new PageImpl<>(results.getResults(), pageable, results.getTotal());
        this.totalCount = results.getTotal();
    }
}
