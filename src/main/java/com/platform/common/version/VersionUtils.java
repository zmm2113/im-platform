package com.platform.common.version;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.enums.ResultCodeEnum;
import com.platform.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 版本比较
 */
@Slf4j
@Component
public class VersionUtils {

    /**
     * 比较版本大小
     * <p>
     * 说明：支n位基础版本号+1位子版本号
     * 示例：1.0.2>1.0.1
     *
     * @param version1 版本1
     * @param version2 版本2
     * @return 0:相同 >0:大于 <0:小于
     */
    public static int compareTo(String version1, String version2, String uri) {
        if (!matchVersion(version1)) {
            log.error("RequestURI：" + uri);
            log.error("传入版本格式有误version1-{}", version1);
            throw new BaseException(ResultCodeEnum.VERSION);
        }
        if (!matchVersion(version2)) {
            log.error("RequestURI：" + uri);
            log.error("系统版本格式有误version2-{}", version2);
            throw new BaseException(ResultCodeEnum.VERSION);
        }
        if (version1.equals(version2)) {
            return 0;
        }
        return versionStrToNum(version1) - versionStrToNum(version2);
    }

    /**
     * 比较版本大小
     * <p>
     * 说明：支n位基础版本号+1位子版本号
     * 示例：1.0.2>1.0.1
     *
     * @param version1 版本1
     * @param version2 版本2
     * @return 0:相同 >0:大于 <0:小于
     */
    public static int compareTo(String version1, String version2) {
        return compareTo(version1, version2, "-");
    }

    /**
     * 版本号转换为数字
     *
     * @param versionStr
     * @return
     */
    public static int versionStrToNum(String versionStr) {
        List<String> dataList = StrUtil.splitTrim(versionStr, ".");
        StringBuilder builder = new StringBuilder()
                .append(dataList.get(0))
                .append(dataList.get(1))
                .append(String.format("%03d", Integer.valueOf(dataList.get(2))));
        return Integer.valueOf(builder.toString());
    }

    /**
     * 匹配版本
     *
     * @param version
     * @return
     */
    private static boolean matchVersion(String version) {
        return ReUtil.isMatch("\\d{1,3}(\\.\\d{1,3}){2}", StringUtils.isEmpty(version) ? "" : version);
    }

    // 匹配器
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 验证地址
     */
    public static boolean verifyUrl(String currentUrl, List<String> whiteList) {
        for (String url : whiteList) {
            if (pathMatcher.match(url, currentUrl)) {
                return true;
            }
        }
        return false;
    }

}