package com.xwc.support.task;

import com.xwc.support.task.models.TaskContext;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 14:48
 * 描述：线程执行处理器 用于拦截线程
 * 在任务提交到线程池的时候被调用 addTask
 * 在任务被线程池执行的时候被调用 beforeExecuteProcessor
 * 在任务被线程池执行完毕的时候被调用  afterExecuteProcessor
 */
public interface ExecutorPostProcessor {

    /**
     * 在线程添加到任务队列的时候被调用
     *
     * @param taskContext
     */
    void addTask(TaskContext taskContext);

    /**
     * 任务执行之前执行被调用
     *
     * @param t
     * @param taskContext 执行的任务
     */
    void beforeExecuteProcessor(Thread t, TaskContext taskContext);

    /**
     * 任务执行完之后被调用，异常也会执行
     *
     * @param t
     * @paramt askContext 执行的任务
     */
    void afterExecuteProcessor(TaskContext taskContext, Throwable t);

}
