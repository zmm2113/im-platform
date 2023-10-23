package com.platform.common.web.page;

import com.platform.common.utils.ServletUtils;

/**
 * 表格数据处理
 */
public class TableSupport {
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY = "orderBy";

    /**
     * 排序方向 "desc" 或者 "asc"
     */
    public static final String ORDER_SORT = "orderSort";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM, 1));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE, 10));
        pageDomain.setOrderBy(ServletUtils.getParameter(ORDER_BY));
        pageDomain.setOrderSort(ServletUtils.getParameter(ORDER_SORT, "asc"));
        return pageDomain;
    }

}
