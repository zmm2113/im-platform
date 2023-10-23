package com.platform.common.web.domain;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * 自定义时间转换
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String date = jsonParser.getText();
        return parseDate(date);
    }

    /**
     * 前台日期格式
     */
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};


    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return DateUtil.parse(obj.toString(), parsePatterns);
        } catch (DateException e) {
            return null;
        }
    }

}
