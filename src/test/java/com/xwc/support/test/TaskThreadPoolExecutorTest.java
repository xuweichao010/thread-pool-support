package com.xwc.support.test;

import com.xwc.support.task.TaskThreadPoolExecutor;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类描述：测试线程池的功能
 * 作者：徐卫超 (cc)
 * 时间 2022/4/19 10:13
 */
public class TaskThreadPoolExecutorTest {

    @Test
    public void taskThreadPoolExecutorTest() {
        TaskThreadPoolExecutor taskThreadPoolExecutorTest = new TaskThreadPoolExecutor(
                10, 20, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        for (int i = 0; i < 1000000; i++) {
            int finalI = i;
            taskThreadPoolExecutorTest.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(new Random(20).nextInt());
                    if (finalI % 10000 == 0) {
                        System.out.println("任务执行数量 ====>>>>>> " + finalI);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            new Random(10).nextInt();
        }

        try {
            TimeUnit.SECONDS.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
