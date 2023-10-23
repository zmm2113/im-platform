package com.platform.common.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.platform.common.core.EnumUtils;
import com.platform.common.enums.GenderEnum;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.web.domain.AjaxResult;
import com.platform.common.web.domain.JsonDateDeserializer;
import com.platform.common.web.page.PageDomain;
import com.platform.common.web.page.TableDataInfo;
import com.platform.common.web.page.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 */
@Slf4j
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(JsonDateDeserializer.parseDate(text));
            }
        });
        // YesOrNoEnum 类型转换
        binder.registerCustomEditor(YesOrNoEnum.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(EnumUtils.toEnum(YesOrNoEnum.class, text));
            }
        });
        // GenderTypeEnum 类型转换
        binder.registerCustomEditor(GenderEnum.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(EnumUtils.toEnum(GenderEnum.class, text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.getPageDomain();
        startPage(PageDomain.escapeOrderBySql(pageDomain.getOrderBy()));
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage(String orderBy) {
        PageDomain pageDomain = TableSupport.getPageDomain();
        PageHelper.startPage(pageDomain.getPageNum(), pageDomain.getPageSize(), StrUtil.toUnderlineCase(orderBy));
    }

    /**
     * 设置排序分页数据
     */
    protected void orderBy(String orderBy) {
        PageHelper.orderBy(StrUtil.toUnderlineCase(orderBy));
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        return new TableDataInfo(list, new PageInfo(list).getTotal());
    }

    protected TableDataInfo getDataTable(List<?> list, PageDomain pageDomain) {
        return getDataTable(CollUtil.sub(list, pageDomain.getPageStart(), pageDomain.getPageEnd()));
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(PageInfo<?> list) {
        return new TableDataInfo(list.getList(), list.getTotal());
    }

    /**
     * 响应返回结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.fail();
    }

}
