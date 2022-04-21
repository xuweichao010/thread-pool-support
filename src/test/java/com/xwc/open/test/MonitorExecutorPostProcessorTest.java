package com.xwc.open.test;

import com.xwc.open.executors.TaskThreadPoolExecutor;
import com.xwc.open.executors.support.MonitorExecutorPostProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类描述：ceshi
 * 作者：徐卫超 (cc)
 * 时间 2022/4/19 15:46
 */
public class MonitorExecutorPostProcessorTest {
    private TaskThreadPoolExecutor taskThreadPoolExecutorTest;

    @Before
    public void init() {
        taskThreadPoolExecutorTest = new TaskThreadPoolExecutor(
                10, 20, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        MonitorExecutorPostProcessor monitor = new MonitorExecutorPostProcessor(2, TimeUnit.SECONDS);
        taskThreadPoolExecutorTest.addExecutorPostProcessor(monitor);
    }

    @Test
    public void taskThreadPoolExecutorTest() throws InterruptedException {
        for (int i = 0; i < 1000000; i++) {
            int finalI = i;
            taskThreadPoolExecutorTest.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(50) + 30);
                    if (finalI % 10000 == 0) {
                        System.out.println("任务执行数量 ====>>>>>> " + finalI);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(5));
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
