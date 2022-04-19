package com.xwc.open.executors.support;



import com.xwc.open.executors.ExecutorPostProcessor;
import com.xwc.open.executors.models.ExecutorPerformanceMetrical;
import com.xwc.open.executors.models.TaskContext;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 16:14
 * 描述：性能监控执行器
 */
public class MonitorExecutorPostProcessor implements ExecutorPostProcessor {
    //任务创建时间
    private static final String CREATE_TIME = "monitorExecutorPostProcessor.createTime";
    //任务执行时间
    private static final String EXEC_TIME = "monitorExecutorPostProcessor.execTime";

    private volatile ExecutorPerformanceMetrical metrical;

    private boolean isAutoOut = false;

    private AtomicLong waitingCount = new AtomicLong();
    private AtomicLong runningCount = new AtomicLong();

    /**
     * 自动输出性能日志的时间单位 当单位为空 或者时间为0时 不输出日志
     */

    public MonitorExecutorPostProcessor(TimeUnit timeUnit, long time, boolean isAutoOut) {
        if (timeUnit == null || time < 0) return;
        this.metrical = new ExecutorPerformanceMetrical();
        this.isAutoOut = isAutoOut;
        if (!isAutoOut) return;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ExecutorPerformanceMetrical outMetrical = MonitorExecutorPostProcessor.this.metrical;
                metrical = new ExecutorPerformanceMetrical();
                System.out.println(printMetric("任务", outMetrical));
            }
        }, timeUnit.toMillis(time), timeUnit.toMillis(time));
    }

    public MonitorExecutorPostProcessor() {
        this(null, -1, false);
    }


    public ExecutorPerformanceMetrical getMetrical() {
        if (isAutoOut) throw new RuntimeException("无法获取自动输出的日志信息");
        ExecutorPerformanceMetrical outMetrical = this.metrical;
        metrical = new ExecutorPerformanceMetrical();
        return outMetrical;
    }

    @Override
    public void addTask(TaskContext taskContext) {
        if (metrical != null) {
            waitingCount.incrementAndGet();
            taskContext.setParam(CREATE_TIME, System.nanoTime());
        }
    }

    @Override
    public void beforeExecuteProcessor(Thread t, TaskContext taskContext) {
        if (metrical == null) return;
        taskContext.setParam(EXEC_TIME, System.nanoTime());
        waitingCount.decrementAndGet();
        runningCount.incrementAndGet();
    }

    @Override
    public void afterExecuteProcessor(TaskContext taskContext, Throwable t) {
        if (metrical == null) return;
        Long execTime = taskContext.get(EXEC_TIME);
        Long createTime = taskContext.get(CREATE_TIME);
        runningCount.decrementAndGet();
        if (execTime == null || createTime == null) return;
        long finishedTime = System.nanoTime();
        metrical.setRunning(finishedTime - execTime).setWaiting(execTime - createTime).countIncrement();
    }


    public String printMetric(String title, ExecutorPerformanceMetrical outMetrical) {
        StringBuilder sbBuf = new StringBuilder();
        sbBuf.append(String.format("-----%s-----\n" +
                        "Perf: %6.2f Hz | Running: %d | Waiting: %d | Finished: %d\n" +
                        "Running avg:%6.3fs | min:%4dms | max:%4dms\n" +
                        "Waiting avg:%6.3fs | min:%4dms | max:%4dms\n" +
                        "-----%s-----",
                title,
                outMetrical.calculatePerformance(),
                this.runningCount.get(),
                this.waitingCount.get(),
                outMetrical.getCount(),

                outMetrical.runningAvg() / 1000_000_000D,
                outMetrical.getRunning().getMin() / 1000_000,
                outMetrical.getRunning().getMax() / 1000_000,

                outMetrical.waitingAvg() / 1000_000_000D,
                outMetrical.getWaiting().getMin() / 1000_000,
                outMetrical.getWaiting().getMax() / 1000_000,
                title.replaceAll(".", "+")
        ));
        return sbBuf.toString();
    }


}
