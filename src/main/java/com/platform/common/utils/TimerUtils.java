package com.platform.common.utils;

import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 环形队列--定时器
 *
 * @author 考拉
 * @email 939313737@qq.com
 * @date 2017年8月26日 All rights Reserved, Designed By www.q3z3.com
 */
@Slf4j
public class TimerUtils {

    private HashedWheelTimer timer;

    private TimerUtils() {
        // 精度1秒，一共512格
        this.timer = new HashedWheelTimer(1, TimeUnit.SECONDS);
    }

    private static class SingletonHolder {
        private static final TimerUtils singleton = new TimerUtils();
    }

    public static TimerUtils instance() {
        return SingletonHolder.singleton;
    }

    public void addTask(TimerTask task, long delay, TimeUnit unit) {
        log.info("添加定时任务, 延迟：{} {}", delay, unit.name());
        timer.newTimeout(task, delay, unit);
    }

}
