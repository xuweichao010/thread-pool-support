package com.xwc.support.task;


import com.xwc.support.task.models.TaskContext;
import com.xwc.support.task.support.CommonRejectedExecutionHandlerProxy;

import java.util.List;
import java.util.concurrent.*;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 14:47
 * 描述：
 */
public class TaskThreadPoolExecutor extends ThreadPoolExecutor {
    /**
     * 线程的声明周期处理器
     */
    private final List<ExecutorPostProcessor> executorPostProcessorList = new CopyOnWriteArrayList<>();

    /**
     * 线程的默认拒绝策略
     */
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();


    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     */
    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(),
                defaultHandler);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     * @param threadFactory   线程的创建工厂
     */
    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     * @param handler         拒绝策略
     */
    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), handler);
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     * @param handler         拒绝策略
     * @param threadFactory   线程的创建工厂
     * @param handler         拒绝策略
     */
    public TaskThreadPoolExecutor(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
                (handler instanceof RejectedExecutionHandlerProxy) ? handler : new CommonRejectedExecutionHandlerProxy(handler));
    }


    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     * @param handler         拒绝策略
     * @param threadFactory   线程的创建工厂
     * @param handler         拒绝策略
     * @param processor       线程的执行的处理器 可以拦截线程任务的执行周期
     */
    public TaskThreadPoolExecutor(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler,
                                  ExecutorPostProcessor processor) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
                (handler instanceof RejectedExecutionHandlerProxy) ? handler : new CommonRejectedExecutionHandlerProxy(handler));
        this.addExecutorPostProcessor(processor);
    }


    /**
     * 创建线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务的等待队列
     * @param handler         拒绝策略
     * @param threadFactory   线程的创建工厂
     * @param handler         拒绝策略
     * @param processors      线程的执行的处理器 可以拦截线程任务的执行周期
     */
    public TaskThreadPoolExecutor(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,
                                  ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler,
                                  List<ExecutorPostProcessor> processors) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
                (handler instanceof RejectedExecutionHandlerProxy) ? handler : new CommonRejectedExecutionHandlerProxy(handler));
        this.addExecutorPostProcessors(processors);
    }


    /**
     * 线程任务执行之前会被调用
     *
     * @param t 调用的线程对象
     * @param r 运行的任务对象
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (executorPostProcessorList == null) return;
        executorPostProcessorList.forEach(executorPostProcessor -> {
            if (r instanceof TaskContext) {
                executorPostProcessor.beforeExecuteProcessor(t, (TaskContext) r);
            }

        });
    }

    /**
     * 线程任务执行任务之后会被调用
     *
     * @param t 调用的线程对象
     * @param r 运行的任务对象
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (executorPostProcessorList == null) return;
        for (int i = executorPostProcessorList.size() - 1; i >= 0; i--) {
            if (r instanceof TaskContext) {
                executorPostProcessorList.get(i).afterExecuteProcessor((TaskContext) r, t);
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

    /**
     * 添加多个线程处理器
     *
     * @param postProcessors 线程处理器
     */
    public void addExecutorPostProcessors(List<ExecutorPostProcessor> postProcessors) {
        postProcessors.forEach(this::addExecutorPostProcessor);
    }

    /**
     * 获取线程的处理器
     *
     * @return 线程处理器
     */
    public List<ExecutorPostProcessor> getExecutorPostProcessorList() {
        return executorPostProcessorList;
    }

    /**
     * 执行一个任务
     *
     * @param command 被执行的任务
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) throw new NullPointerException();
        TaskContext taskContext = new TaskContext(command);
        executorPostProcessorList.forEach(item -> item.addTask(taskContext));
        super.execute(taskContext);
    }


    /**
     * 提交一个任务
     *
     * @param task 被执行的任务
     */
    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        TaskContext taskContext = new TaskContext(task);
        executorPostProcessorList.forEach(item -> item.addTask(taskContext));
        return super.submit(new TaskContext(task));
    }


}
