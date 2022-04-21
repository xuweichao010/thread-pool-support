package com.xwc.support.task.models;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：徐卫超
 * 时间：2020/7/3 15:35
 * 描述：线程上下文的信心，提供给ExecutorPostProcessor接口在线程的不通执行周期存储所需要的环境信息
 */
public class TaskContext implements Runnable {

    private Runnable runnable;

    public TaskContext(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * 存储线程运行时需要的环境变量
     */
    private Map<String, Object> param;

    public TaskContext setParam(String key, Object value) {
        if (param == null) param = new HashMap<>();
        param.put(key, value);
        return this;
    }

    @SuppressWarnings("all")
    public <T> T get(String key) {
        Object value = param.get(key);
        if (value == null) return null;
        return (T) value;
    }


    @Override
    public void run() {
        runnable.run();
    }
}
