package com.ai.travel.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        
        // 计算总页数
        if (pageSize != null && pageSize > 0) {
            result.setPages((int) Math.ceil((double) total / pageSize));
        } else {
            result.setPages(1);
        }
        
        return result;
    }

    /**
     * 空分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> empty() {
        PageResult<Object> empty = new PageResult<>();
        empty.setTotal(0L);
        empty.setPages(1);
        empty.setPageNum(1);
        empty.setPageSize(10);
        empty.setList(List.of());
        return (PageResult<T>) empty;
    }

}