package com.xwc.open.executors;

import com.xwc.open.executors.models.TaskContext;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 14:48
 * 描述：线程执行拦截器
 */
public interface ExecutorPostProcessor {

    void addTask(TaskContext taskContext);

    /**
     * 任务执行之前执行
     *
     * @param t
     * @param taskContext 执行的任务
     */
    void beforeExecuteProcessor(Thread t, TaskContext taskContext);

    /**
     * 任务执行完之后执行，异常也会执行
     *
     * @param t
     * @paramt askContext 执行的任务
     */
    void afterExecuteProcessor(TaskContext taskContext, Throwable t);

}
