package com.xwc.support.task.support;


import com.xwc.support.task.ExecutorPostProcessor;
import com.xwc.support.task.ThreadPoolAware;
import com.xwc.support.task.models.ExecutorPerformanceMetrical;
import com.xwc.support.task.models.TaskContext;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 16:14
 * 描述：用于监控线程池的运行时间信息，并统计这些信息的：
 * -----任务-----
 * Perf: 307.10 Hz | Running: 20 | Waiting: 93 | Finished: 619
 * Running avg: 0.055s | min:  29ms | max:  80ms
 * Waiting avg: 0.216s | min:   0ms | max: 317ms
 * -----++-----
 */
public class MonitorExecutorPostProcessor implements ExecutorPostProcessor, ThreadPoolAware {
    //任务创建时间
    private static final String CREATE_TIME = "monitorExecutorPostProcessor.createTime";
    //任务执行时间
    private static final String EXEC_TIME = "monitorExecutorPostProcessor.execTime";
    // 性能指标
    private volatile ExecutorPerformanceMetrical metrical;
    // 被监控的线程池
    private ThreadPoolExecutor threadPoolExecutor;

    private AtomicLong runningCount = new AtomicLong();

    /**
     * 构建一个监控任务执行处理器器
     * 当 time 大于零的时候会根据配置的时间自动输出
     * 当 time 为小于等于零的时候将不在自动输出 交给用户主动调用获取
     *
     * @param timeUnit 自动输出的时间单位
     * @param time     自动输出的阈值
     */

    public MonitorExecutorPostProcessor(long time, TimeUnit timeUnit) {
        this.metrical = new ExecutorPerformanceMetrical();
        if (timeUnit == null || time <= 0) return;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(printMetric("任务", getMetrical()));
            }
        }, timeUnit.toMillis(time), timeUnit.toMillis(time));
    }

    public MonitorExecutorPostProcessor() {
        this(-1, null);
    }


    public ExecutorPerformanceMetrical getMetrical() {
        ExecutorPerformanceMetrical outMetrical = this.metrical;
        this.metrical = new ExecutorPerformanceMetrical();
        outMetrical.setEndTime(System.nanoTime());
        outMetrical.setConcurrentRunning(this.threadPoolExecutor.getActiveCount());
        outMetrical.setConcurrentWaiting(this.threadPoolExecutor.getQueue().size());
        return outMetrical;
    }

    @Override
    public void addTask(TaskContext taskContext) {
        if (metrical != null) {
            taskContext.setParam(CREATE_TIME, System.nanoTime());
        }
    }

    @Override
    public void beforeExecuteProcessor(Thread t, TaskContext taskContext) {
        if (metrical == null) return;
        taskContext.setParam(EXEC_TIME, System.nanoTime());
        runningCount.incrementAndGet();
    }

    @Override
    public void afterExecuteProcessor(TaskContext taskContext, Throwable t) {
        if (metrical == null) return;
        Long execTime = taskContext.get(EXEC_TIME);
        Long createTime = taskContext.get(CREATE_TIME);
        if (execTime == null || createTime == null) return;
        long finishedTime = System.nanoTime();
        metrical.setRunning(finishedTime - execTime).setWaiting(execTime - createTime).countIncrement();
    }


    public String printMetric(String title, ExecutorPerformanceMetrical outMetrical) {
        StringBuilder sbBuf = new StringBuilder();
        sbBuf.append(String.format(">>>>>>>>>>>>\t %s(%3.2f s) \t<<<<<<<<<<<<<<\n" +
                        "Perf: %6.2f Hz | Running: %d | Waiting: %d | Finished: %d\n" +
                        "Running avg:%6.3fs | min:%4dms | max:%4dms\n" +
                        "Waiting avg:%6.3fs | min:%4dms | max:%4dms\n",
                title,
                outMetrical.timeDuration() * 1.D / 1000_000_000,
                outMetrical.calculatePerformance(),
                outMetrical.getConcurrentRunning(),
                outMetrical.getConcurrentWaiting(),
                outMetrical.getCount(),

                outMetrical.runningAvg() / 1000_000_000D,
                outMetrical.getRunning().getMin().get() / 1000_000,
                outMetrical.getRunning().getMax().get() / 1000_000,

                outMetrical.waitingAvg() / 1000_000_000D,
                outMetrical.getWaiting().getMin().get() / 1000_000,
                outMetrical.getWaiting().getMax().get() / 1000_000
        ));
        return sbBuf.toString();
    }


    @Override
    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
