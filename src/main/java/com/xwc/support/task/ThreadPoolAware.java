package com.xwc.support.task;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 类描述：在为线程池添加处理器会调用实现了这个接口的对象的 setThreadPoolExecutor方法
 * 用于处理器获取线程池的信息
 * 作者：徐卫超 (cc)
 * 时间 2022/4/19 16:20
 */
public interface ThreadPoolAware {

    void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor);
}
