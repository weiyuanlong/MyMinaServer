package com.aisafer.minasocket.api;

import com.aisafer.minasocket.utils.ConfigValueUtil;
import com.aisafer.minasocket.utils.MyThreadPool;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 初始化系统数据，加载缓存
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 17:22:03
 * @Modified By:
 */
public class InitSorketParam {

    /** 缓存区的公司组织名称 */
    public static Map<Integer,String> departments = new HashMap<>();

    /** 缓存区的车辆 */
    public static Map<Integer,Map> vehicles = new HashMap<>();

    /** 缓存区的车辆类型 */
    public static Map<Integer,String> vehicleTypes = new HashMap<>();

    /** 缓存区的司机 */
    public static Map<Integer,Map> drivers = new HashMap<>();

    /** 缓存区的用户 */
    public static Map<Integer,Map> users = new HashMap<>();

    /**
     * 启动mina服务
     *
     */
    public static void startMina(){
        // 服务器端的主要对象
        IoAcceptor acceptor = new NioSocketAcceptor();

        // 设置过滤器
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());

        // 协议解析，采用mina现成的UTF-8字符串处理方式
        TextLineCodecFactory lineCodec=new TextLineCodecFactory(Charset.forName("UTF-8"));
        lineCodec.setDecoderMaxLineLength(1024*1024); //1M
        lineCodec.setEncoderMaxLineLength(1024*1024); //1M

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineCodec));

        KeepAliveMessageFactory heartBeatFactory = new MyKeepAliveMessageFactory();
        // 设置心跳检测   1.心跳检测数据处理实现类  2.服务器状态  进入空闲后发送心跳请求  3.心跳检测超时处理实现类  4.发送心跳检测间隔  5.多久未响应判断为超时
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,IdleStatus.BOTH_IDLE,new MyKeepAliveRequestTimeoutHandler(),ConfigValueUtil.SEND_HEART_INTERVAL,ConfigValueUtil.SEND_HEART_TIMEOUT);
        // 设置是否forward到下一个filter
        heartBeat.setForwardEvent(true);
        acceptor.getFilterChain().addLast("heartbeat", heartBeat);

        // 设置消息处理类（创建、关闭Session，可读可写等等，继承自接口IoHandler）
        acceptor.setHandler(new MyIoHandler());

        // 设置接受缓冲区大小
        acceptor.getSessionConfig().setReadBufferSize(2048);

        // 读写通道无任何操作15S就进入空闲状态
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,ConfigValueUtil.SESSION_FREE);

        try {
            // 服务器开始监听
            acceptor.bind( new InetSocketAddress(ConfigValueUtil.PORT) );
            // 开始刷新缓存
            initCache();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化缓存区
     *
     */
    private static void initCache() {
        // 加载公司组织和车辆缓存数据
        initDepartment();
        // 开始加载推送数据
        // initGetData();
    }

    /**
     * 初始化组织 开启定时任务 没隔一定时间刷新一次组织数据
     *
     */
    private static void initDepartment() {
        ScheduledThreadPoolExecutor threadPoolExecutor = MyThreadPool.getThreadPoolExecutor();
        // 开启加载刷新公司组织和车辆数据 线程
        threadPoolExecutor.scheduleAtFixedRate(new InitDepartmentRunnale(),0,ConfigValueUtil.FLASH_DATA,TimeUnit.MINUTES);
    }

}
