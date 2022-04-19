package com.xwc.open.executors;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 类描述：获取线程池的信息
 * 作者：徐卫超 (cc)
 * 时间 2022/4/19 16:20
 */
public interface ThreadPoolAware {

    void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor);
}
