package com.platform.modules.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.exception.BaseException;
import com.platform.common.utils.redis.RedisUtils;
import com.platform.modules.chat.config.AmapConfig;
import com.platform.modules.chat.service.ChatWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 天气预报-服务层实现
 * q3z3
 * </p>
 */
@Service("chatWeatherService")
public class ChatWeatherServiceImpl implements ChatWeatherService {

    // 文档地址 https://lbs.amap.com/api/webservice/guide/api/weatherinfo

    /**
     * 接口地址
     */
    private final static String URL = "https://restapi.amap.com/v3/weather/weatherInfo?city=CITY&&key=KEY&extensions=EXT";
    private final static String EXT_BASE = "base";

    @Autowired
    private AmapConfig amapConfig;

    @Autowired
    private RedisUtils redisUtils;

    private JSONArray doQuery(String city, String extensions) {
        String key = StrUtil.format(AppConstants.REDIS_MP_WEATHER, city, extensions);
        if (redisUtils.hasKey(key)) {
            return JSONUtil.parseArray(redisUtils.get(key));
        }
        String url = URL.replace("CITY", city).replace("KEY", amapConfig.getKey()).replace("EXT", extensions);
        String result = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if (1 != jsonObject.getInt("status")) {
            throw new BaseException("天气接口异常，请稍后再试");
        }
        JSONArray jsonArray = jsonObject.getJSONArray(EXT_BASE.equals(extensions) ? "lives" : "forecasts");
        if ("[[]]".equals(jsonArray.toString())) {
            return new JSONArray();
        }
        redisUtils.set(key, JSONUtil.toJsonStr(jsonArray), AppConstants.REDIS_MP_WEATHER_TIME, TimeUnit.MINUTES);
        return jsonArray;
    }

    @Override
    public List<JSONObject> queryByCityName(String cityName) {
        JSONArray jsonArray = doQuery(cityName, EXT_BASE);
        return jsonArray.toList(JSONObject.class);
    }

}
