package com.xwc.open.executors;


import com.xwc.open.executors.models.TaskContext;
import com.xwc.open.executors.support.CommonRejectedExecutionHandlerProxy;

import java.util.List;
import java.util.concurrent.*;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 14:47
 * 描述：
 */
public class TaskThreadPoolExecutor extends ThreadPoolExecutor {
    private final List<ExecutorPostProcessor> executorPostProcessorList = new CopyOnWriteArrayList<>();

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();


    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(),
                defaultHandler);
    }

    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), handler);
    }

    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new CommonRejectedExecutionHandlerProxy(handler));
    }


    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (executorPostProcessorList == null) return;
        executorPostProcessorList.forEach(executorPostProcessor -> {
            if (r instanceof TaskContext) {
                executorPostProcessor.beforeExecuteProcessor(t, (TaskContext) r);
            }

        });
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (executorPostProcessorList == null) return;
        for (int i = executorPostProcessorList.size() - 1; i >= 0; i--) {
            try {
                if (r instanceof TaskContext) {
                    executorPostProcessorList.get(i).afterExecuteProcessor((TaskContext) r, t);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加一个线程处理器
     *
     * @param postProcessor 线程处理器
     */
    public void addExecutorPostProcessor(ExecutorPostProcessor postProcessor) {
        if (postProcessor == null) throw new NullPointerException();
        executorPostProcessorList.add(postProcessor);
        if (postProcessor instanceof ThreadPoolAware) {
            ((ThreadPoolAware) postProcessor).setThreadPoolExecutor(this);
        }
    }

    public List<ExecutorPostProcessor> getExecutorPostProcessorList() {
        return executorPostProcessorList;
    }

    public void execute(Runnable command) {
        if (command == null) throw new NullPointerException();
        TaskContext taskContext = new TaskContext(command);
        executorPostProcessorList.forEach(item -> item.addTask(taskContext));
        super.execute(taskContext);
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        TaskContext taskContext = new TaskContext(task);
        executorPostProcessorList.forEach(item -> item.addTask(taskContext));
        return super.submit(new TaskContext(task));
    }


}
