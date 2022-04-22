package com.xwc.support.task.models;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 20:17
 * 描述：统计类 主要提供给 ExecutorPerformanceMetrical使用
 */

@Data
public class PerformanceMetrical {
    /**
     * 记录任务最小的时间 单位是纳秒
     */
    private AtomicLong min = new AtomicLong();
    /**
     * 记录任务的最大时间 单位是纳秒
     */
    private AtomicLong max = new AtomicLong();
    /**
     * 记录任务的总数量 单位是纳秒
     */
    private AtomicLong sum = new AtomicLong();

    public void set(long time) {
        long expect;
        while ((expect = min.get()) > time || (expect == 0 && time > 0)) {
            min.compareAndSet(expect, time);
        }
        while ((expect = max.get()) < time) {
            max.compareAndSet(expect, time);
        }
        sum.addAndGet(time);
    }
}
