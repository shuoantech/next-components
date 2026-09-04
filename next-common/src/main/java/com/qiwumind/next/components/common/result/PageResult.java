package com.qiwumind.next.components.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "分页结果")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class PageResult<T> implements Serializable {

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long total;

    @Schema(description = "数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(List<T> list, Long total, Integer pageNo, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public PageResult(Long total, Integer pageNo, Integer pageSize) {
        this.list = new ArrayList<>();
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

    public static <T> PageResult<T> empty(Long total, Integer pageNo, Integer pageSize) {
        return new PageResult<>(total, pageNo, pageSize);
    }
}