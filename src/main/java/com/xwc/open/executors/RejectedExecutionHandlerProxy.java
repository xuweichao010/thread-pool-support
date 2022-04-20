package com.xwc.open.executors;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 类描述：
 * 作者：徐卫超 (cc)
 * 时间 2022/4/20 9:45
 */
public abstract class RejectedExecutionHandlerProxy implements RejectedExecutionHandler {


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor instanceof TaskThreadPoolExecutor) {
            TaskThreadPoolExecutor taskThreadPoolExecutor = (TaskThreadPoolExecutor) executor;
            taskThreadPoolExecutor.beforeExecute(Thread.currentThread(), r);
            doRejectedExecution(r, executor);
            taskThreadPoolExecutor.afterExecute(r, null);
        }
    }

    public abstract void doRejectedExecution(Runnable r, ThreadPoolExecutor executor);

}
