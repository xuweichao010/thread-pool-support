package com.xwc.open.executors.support;

import com.xwc.open.executors.RejectedExecutionHandlerProxy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 类描述：通用的异常处理器
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
