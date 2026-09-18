package com.ai.travel.common.vo;

import com.ai.travel.common.constant.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 * @param <T> 数据类型
 */
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
        // 参数校验和默认值处理
        if (pageNum == null || pageNum < 1) {
            pageNum = SystemConstants.DEFAULT_PAGE_NUM;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = SystemConstants.DEFAULT_PAGE_SIZE;
        }
        if (pageSize > SystemConstants.MAX_PAGE_SIZE) {
            pageSize = SystemConstants.MAX_PAGE_SIZE;
        }

        // 计算总页数
        int pages = (int) Math.ceil((double) total / pageSize);
        if (pages < 1 && total > 0) {
            pages = 1;
        }

        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPages(pages);
        result.setList(list);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    /**
     * 创建空分页结果
     */
    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> empty() {
        PageResult<Object> empty = new PageResult<>();
        empty.setTotal(0L);
        empty.setPages(1);
        empty.setPageNum(1);
        empty.setPageSize(SystemConstants.DEFAULT_PAGE_SIZE);
        empty.setList(List.of());
        return (PageResult<T>) empty;
    }

}