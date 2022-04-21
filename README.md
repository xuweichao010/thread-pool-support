## TaskThreadPoolExecutor线程池

```text
    TaskThreadPoolExecutor集成于ThreadPoolExecutor线程池,该线程池添加了拦截任务执行的机制,
它在任务提交时、在任务执行时、在任务执行完成后设立三个拦截点,用于开发人员在这三个拦截点做自己想要做的事情,
后期我也会根据工作中的需要继续完成对该线程池的增强
```

### 线程池的监控

```text
    线程池监控是基于TaskThreadPoolExecutor中的三个拦截点来实现的,我们基于TaskThreadPoolExecutor线程池中
的 ExecutorPostProcessor 接口来完成线程池的监控的 实现类是: MonitorExecutorPostProcessor
 方法来采集任务的各种节点时间。
 MonitorExecutorPostProcessor 自动输出信息如下：
 
     >>>>>>>>>>>>	 任务(2.00 s) 	<<<<<<<<<<<<<<
     Perf: 177.97 Hz | Running: 10 | Waiting: 874 | Finished: 356
     Running avg: 0.056s | min:  30ms | max:  79ms
     Waiting avg: 1.502s | min: 940ms | max:2077ms

输出信息描述：
     任务： 输出Title
     2.00s： 该次任务统计的周期
     Perf:  线程池每秒执行的任务数量
     Running: 线程池正在处理任务的线程数量
     waiting: 等待队列里的线程数量
     Finished: 监控周期内完成的任务数
     Running avg: 0.056s | min:  30ms | max:  79ms  任务执行的平均时间、最小时间、最大时间
     Waiting avg: 1.502s | min: 940ms | max:2077ms  任务等待的平均时间、最小时间、最大时间
```

#### 快速开始

- 通过手动方式添加处理器
   ```java
       taskThreadPoolExecutorTest = new TaskThreadPoolExecutor(
               10, 20, 30, TimeUnit.SECONDS,
               new ArrayBlockingQueue<>(10000),
               Executors.defaultThreadFactory(),
               new ThreadPoolExecutor.CallerRunsPolicy()
       );
       MonitorExecutorPostProcessor monitor = new MonitorExecutorPostProcessor(2, TimeUnit.SECONDS);
       taskThreadPoolExecutorTest.addExecutorPostProcessor(monitor);
   ```

- 通过构造方法添加处理器
   ```java
       taskThreadPoolExecutorTest = new TaskThreadPoolExecutor(
                10, 20, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy(), new MonitorExecutorPostProcessor(2, TimeUnit.SECONDS)
        );
   ```

#### 获取监控信息

- 控制台自动方案输出

  ```java
      // 在构建监控处理器的时候添加输出频率 
      // 监控器会每两秒输出一次线程的运行情况 
      MonitorExecutorPostProcessor monitor=new MonitorExecutorPostProcessor(2,TimeUnit.SECONDS)
  ```

- 用户手动调用方法方案

  ```java
    MonitorExecutorPostProcessor monitor = new MonitorExecutorPostProcessor(-1, TimeUnit.SECONDS);
    // 手动获取任务的信息,统计评率是根据用户调用该方法频率决定的
    ExecutorPerformanceMetrical metrical = monitor.getMetrical();
  ```