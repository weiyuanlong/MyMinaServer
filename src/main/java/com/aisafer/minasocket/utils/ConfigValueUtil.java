package com.aisafer.minasocket.utils;

/**
 * 系统配置参数的工具类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-14 14:05:54
 * @Modified By:
 */
public class ConfigValueUtil {

    /** 端口号 9081 */
    public static final int PORT = 9081;

    /** 线程池核心线程数 50 */
    public static final int POOL_CORE_SIZE = 50;

    /** 连接池最大连接数 100 */
    public static final int MAX_POOL_SIZE = 100;

    /** 非核心线程闲置超时时长 30 秒 */
    public static final long KEEP_ALIVE_TIME = 30;

    /** 推送数据和刷新缓存推送数据时间间隔 180 秒 */
    public static final int SEND_DATA_INTERVAL = 180;

    /** 第一次数据推送和刷新推送数据延时时间 60 秒 */
    public static final int INIT_SEND_DATA = 60;

    /** 刷新基础信息缓存数据 60 分钟 */
    public static final int FLASH_DATA = 60;

    /** 服务器无操作进入空闲状态时间 15 秒 */
    public static final int SESSION_FREE = 15;

    /** 发送心跳时间间隔 10 秒 */
    public static final int SEND_HEART_INTERVAL = 10;

    /** 判定心跳超时时间 3 秒 */
    public static final int SEND_HEART_TIMEOUT = 3;

}
