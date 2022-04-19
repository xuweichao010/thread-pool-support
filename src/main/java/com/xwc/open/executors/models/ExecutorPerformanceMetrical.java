package com.xwc.open.executors.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 20:40
 * 描述：线程池性能对象
 */
public class ExecutorPerformanceMetrical {
    /**
     * 执行任务的总数量
     */
    private AtomicInteger count = new AtomicInteger();
    /**
     * 记录的开始时间
     */
    private long initTime = System.nanoTime();
    /**
     * 任务等待的相关指标
     */
    private PerformanceMetrical waiting = new PerformanceMetrical();
    /**
     * 任务执行的相关指标
     */
    private PerformanceMetrical running = new PerformanceMetrical();

    public ExecutorPerformanceMetrical setWaiting(long waitingTime) {
        waiting.set(waitingTime);
        return this;
    }

    public ExecutorPerformanceMetrical setRunning(long runningTime) {
        running.set(runningTime);
        return this;
    }

    public PerformanceMetrical getWaiting() {
        return waiting;
    }

    public PerformanceMetrical getRunning() {
        return running;
    }

    public ExecutorPerformanceMetrical countIncrement() {
        count.incrementAndGet();
        return this;
    }

    public int getCount() {
        return count.get();
    }

    public long waitingAvg() {
        if (count.get() == 0) return 0;
        return waiting.getSum() / count.get();
    }

    public long runningAvg() {
        if (count.get() == 0) return 0;
        return running.getSum() / count.get();
    }

    /**
     * 每秒可以处理的任务数量
     *
     * @return
     */
    public double calculatePerformance() {
        if (count.get() == 0) return 0D;
        return count.get() * 1000_000_000L * 1D / (System.nanoTime() - initTime);
    }
}
