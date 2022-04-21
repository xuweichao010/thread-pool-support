package com.xwc.support.task.support;

import com.xwc.support.task.RejectedExecutionHandlerProxy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 类描述：通用的异常处理器
 * 主要处理TaskThreadPoolExecutor和ThreadPoolExecutor两个线程池的因为业务特性不做的一个兼容方案
 * 作者：徐卫超 (cc)
 * 时间 2022/4/20 9:56
 */
public class CommonRejectedExecutionHandlerProxy extends RejectedExecutionHandlerProxy {

    private final RejectedExecutionHandler rejectedExecutionHandler;

    @Override
    public void doRejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        rejectedExecutionHandler.rejectedExecution(r, executor);
    }

    public CommonRejectedExecutionHandlerProxy(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }
}
