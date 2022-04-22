package com.xwc.support.task.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 20:40
 * 描述：线程池性能对象主要提供给 MonitorExecutorPostProcessor使用
 */
public class ExecutorPerformanceMetrical {
    /**
     * 执行任务的总数量
     */
    private AtomicInteger count = new AtomicInteger();
    /**
     * 记录的开始时间
     */
    private long startTime = System.nanoTime();
    /**
     * 记录结束的时间
     */
    private long endTime = System.nanoTime();

    /**
     * 任务等待的相关指标
     */
    private PerformanceMetrical waiting = new PerformanceMetrical();
    /**
     * 任务执行的相关指标
     */
    private PerformanceMetrical running = new PerformanceMetrical();

    /**
     * 当前线程并行的任务数量
     */
    private int concurrentRunning;

    /**
     * 当前线程等待的任务数量
     */
    private int concurrentWaiting;

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
        return waiting.getSum().get() / count.get();
    }

    public long runningAvg() {
        if (count.get() == 0) return 0;
        return running.getSum().get() / count.get();
    }

    public int getConcurrentRunning() {
        return concurrentRunning;
    }

    public int getConcurrentWaiting() {
        return concurrentWaiting;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setWaiting(PerformanceMetrical waiting) {
        this.waiting = waiting;
    }

    public void setRunning(PerformanceMetrical running) {
        this.running = running;
    }

    public void setConcurrentRunning(int concurrentRunning) {
        this.concurrentRunning = concurrentRunning;
    }

    public void setConcurrentWaiting(int concurrentWaiting) {
        this.concurrentWaiting = concurrentWaiting;
    }

    /**
     * 每秒可以处理的任务数量
     *
     * @return
     */
    public double calculatePerformance() {
        if (count.get() == 0) return 0D;
        return count.get() * 1000_000_000L * 1D / this.timeDuration();
    }

    /**
     * 返回任务监控的时间 单位是纳秒
     */
    public long timeDuration() {
        return this.endTime - startTime;
    }
}
