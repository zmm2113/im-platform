package com.platform.common.version;

import com.platform.common.constant.HeadConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Getter
public class VersionCondition implements RequestCondition<VersionCondition> {

    private String version;

    public VersionCondition(String version) {
        this.version = version;
    }

    //将不同的筛选条件合并,这里采用的覆盖，即后来的规则生效
    @Override
    public VersionCondition combine(VersionCondition other) {
        return new VersionCondition(other.getVersion());
    }

    //根据request查找匹配到的筛选条件
    @Override
    public VersionCondition getMatchingCondition(HttpServletRequest request) {
        String version = request.getHeader(HeadConstant.VERSION);
        //  如果请求的版本号大于配置版本号， 则满足，即与请求的
        if (VersionUtils.compareTo(version, this.version, request.getRequestURI()) >= 0) {
            return this;
        }
        return null;
    }

    //实现不同条件类的比较，从而实现优先级排序,返回值最小匹配this
    @Override
    public int compareTo(VersionCondition other, HttpServletRequest request) {
        return VersionUtils.compareTo(other.getVersion(), this.version, request.getRequestURI());
    }

}