package com.aisafer.minasocket.utils;

import java.util.concurrent.*;

/**
 * 获取线程池工具类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 10:23:02
 * @Modified By:
 */
public class MyThreadPool {

    /** 线程池对象 */
    private static ScheduledThreadPoolExecutor threadPoolExecutor;

    /** 线程池核心连接数 */
    private static int CORE_POOL_SIZE = ConfigValueUtil.POOL_CORE_SIZE;

    /** 线程池最大连接数 */
    private static int MAX_POOL_SIZE = ConfigValueUtil.MAX_POOL_SIZE;

    /** 非核心线程闲置超时时长 */
    private static long KEEP_ALIVE_TIME = ConfigValueUtil.KEEP_ALIVE_TIME;

    static {
        System.out.println("初始化线程池  线程池核心线程数 " + CORE_POOL_SIZE);
        threadPoolExecutor  = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        setThreadPoolParam(threadPoolExecutor);
    }

    /**
     * 获取线程池
     *
     * @return
     */
    public static ScheduledThreadPoolExecutor getThreadPoolExecutor() {
        if(threadPoolExecutor != null)
            return threadPoolExecutor;

        threadPoolExecutor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        setThreadPoolParam(threadPoolExecutor);
        return threadPoolExecutor;
    }

    /**
     * 设置线程池基本参数
     *
     * @param threadPoolParam
     */
    private static void setThreadPoolParam(ThreadPoolExecutor threadPoolParam) {
        threadPoolParam.setMaximumPoolSize(MAX_POOL_SIZE);
        threadPoolParam.setKeepAliveTime(KEEP_ALIVE_TIME,TimeUnit.SECONDS);
    }

}
