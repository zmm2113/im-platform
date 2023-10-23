package com.platform.common.web.page;

import com.platform.common.enums.ResultCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

/**
 * 表格分页数据对象
 */
@Data
@Accessors(chain = true) // 链式调用
public class TableDataInfo extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String ROWS_TAG = "rows";

    /**
     * 数据对象
     */
    public static final String TOTAL_TAG = "total";

    /**
     * 分页
     */
    public TableDataInfo(List<?> list, Long total) {
        ResultCodeEnum result = ResultCodeEnum.SUCCESS;
        super.put(CODE_TAG, result.getCode());
        super.put(MSG_TAG, result.getInfo());
        super.put(ROWS_TAG, list);
        super.put(TOTAL_TAG, total.intValue());
    }

    @Override
    public TableDataInfo put(String key, Object value) {
        super.put(key, value);
        return this;
    }

}