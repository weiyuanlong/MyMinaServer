package com.aisafer.minasocket.api;

import com.aisafer.minasocket.service.SeedingService;
import com.aisafer.minasocket.service.SimService;
import com.aisafer.minasocket.service.UserService;
import com.aisafer.minasocket.utils.MyStringUtil;
import com.aisafer.minasocket.utils.MyThreadPool;
import com.aisafer.minasocket.utils.SpringContextUtil;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理用户登录、注销的mina控制类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-05 13:25:48
 * @Modified By:
 */
public class MyIoHandler extends IoHandlerAdapter {

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(MyIoHandler.class);

    /** 保存session的map */
    public static Map<Long,IoSession> sessionMap = new HashMap();

    /** 保存未来线程操作的map */
    public static Map<Long,ScheduledFuture> futureMap = new HashMap();

    /** 保存session所属用户 */
    public static Map<Long,Map> userMap = new HashMap();

    /** 保存对客户端发送心跳检测未响应的持续次数的map */
    public static Map<Long,Integer> hardSendNum = new HashMap();

    /** service层操作用户的对象 */
    private UserService userService = (UserService) SpringContextUtil.getInstanceClass("userService");

    /** service层操作直播的对象 */
    private SeedingService seedingService = (SeedingService) SpringContextUtil.getInstanceClass("seedingService");

    /** service层操作sim卡的对象 */
    private SimService simService = (SimService) SpringContextUtil.getInstanceClass("simService");

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        log.info("----------sessionCreated--------------创建一个地址为   " + session.getRemoteAddress() + "   的客户端连接-------------");
    }

    /**
     * 客户端连接成功 为用户创建一个线程进行发送数据
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    /**
     * 连接出现问题  心跳检测三次无响应  删除map中的线程和session
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);

        Iterator<Long> sessionIterator = sessionMap.keySet().iterator();
        while (sessionIterator.hasNext()) {
            Long mapId = sessionIterator.next();
            if(mapId == session.getId()) {
                sessionIterator.remove();
                sessionMap.remove(mapId);

                ScheduledFuture scheduledFuture = futureMap.get(session.getId());
                scheduledFuture.cancel(true);

                futureMap.remove(mapId);
                userMap.remove(mapId);
                log.info("----------sessionClosed--------------关闭地址为   " + session.getRemoteAddress() + "   的客户端连接-------------");
            }
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        // System.out.println("--------sessionIdle---------------------------------------------服务器进入空闲状态--------------------");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        log.error("--------exceptionCaught-----------------------------地址为：  " + session.getRemoteAddress() + "  的客户端连接出现了异常------");
        log.error(cause.getStackTrace().toString());
        sessionClosed(session);
    }

    /**
     * 处理客户端请求的业务逻辑
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message){
        String param16 = message.toString();
        String paramStr = MyStringUtil.str16ToStr(param16);
        log.info("messageReceived-------------------收到消息" + paramStr);

        try{
            Map paramMap = (Map) JSONUtils.parse(paramStr);
            String id = (String) paramMap.get("id");
            if(("10001").equals(id) || id == "10001") {
                // 为登录验证  获取登录信息 进行登录验证
                loggingMethod(paramMap,session);
            }else if("24001".equals(id) || id == "24001") {
                // 直播
                seedingMethod(paramMap,session);
            }else if("26001".equals(id) || id == "26001") {
                // 获取sim卡流量信息
                getSimFlow(paramMap,session);
            }
        }catch (Exception e) {
            log.error(e.getStackTrace().toString());
            e.printStackTrace();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }

    /**
     * 操作用户登录的方法
     *
     * @param paramMap
     * @param session
     */
    private void loggingMethod(Map paramMap, IoSession session) {
        Map loggingMap = (Map) paramMap.get("data");
        Map resustMap = loggingSure(loggingMap);

        resustMap.put("id","10002");
        // 获取推送数据时间间隔
        Integer timeInterval = (Integer) resustMap.get("timeInterval");

        resustMap.remove("timeInterval");
        String returnStr = JSONUtils.toJSONString(resustMap);

        if(resustMap.get("status").equals("1")) {

            // 登录成功
            sessionMap.put(session.getId(),session);
            log.info("用户登录成功 ， 用户session id为：" + session.getId());
            saveUserSession(loggingMap,session);

            // 获取线程池 为用户创建一个线程发送数据
            ScheduledThreadPoolExecutor threadPoolExecutor = MyThreadPool.getThreadPoolExecutor();
            Runnable sendMessageRunnable = new SendMessageRunnable(session,timeInterval);

            // 开启线程任务 定时执行任务 保存用户与session对应关系
            ScheduledFuture<?> scheduledFuture = threadPoolExecutor.scheduleAtFixedRate(sendMessageRunnable, 0, timeInterval, TimeUnit.SECONDS);
            futureMap.put(session.getId(),scheduledFuture);

            session.write(MyStringUtil.strTo16(returnStr));
        }else {
            // 登录失败
            session.write(MyStringUtil.strTo16(returnStr));
        }
    }

    /**
     * 做登录验证
     *
     * @param loggingMap
     * @return
     */
    private Map loggingSure(Map loggingMap) {
        String username = (String) loggingMap.get("username");
        String password = (String) loggingMap.get("password");

        try {
            Map map = userService.userLogging(username, password);
            return map;
        } catch (Exception e) {
            log.error("做登录验证",e);
            Map resultMap = new HashMap();
            resultMap.put("status","0");
            resultMap.put("message","系统错误");

            log.error(e.getStackTrace().toString());
            return resultMap;
        }
    }

    /**
     * 用户直播操作
     *
     * @param paramMap
     * @param session
     */
    private void seedingMethod(Map paramMap, IoSession session) {
        // 判断用户是否登录
        if(sessionMap.containsKey(session.getId())) {
            // 用户已登录
            try {
                Map resultMap = seedingService.seeding(paramMap, session);
                session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
            }catch (Exception e) {
                log.error("用户直播操作",e);
                Map resultMap = new HashMap();
                resultMap.put("id","24002");
                resultMap.put("status","0");
                resultMap.put("message","系统错误");
                session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
            }
        }else {
            Map resultMap = new HashMap();
            resultMap.put("id","24002");
            resultMap.put("status","0");
            resultMap.put("message","客户端未登录!");
            session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
        }
    }

    /**
     * 获取卡流量信息
     *
     * @param paramMap
     * @param session
     */
    private void getSimFlow(Map paramMap, IoSession session) {
        // 判断用户是否登录
        if(sessionMap.containsKey(session.getId())) {
            // 用户已登录
            try {
                Object simNo = paramMap.get("simNo");
                Map resultMap = simService.getSimFlow(simNo);
                session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
            }catch (Exception e) {
                log.error("获取卡流量信息",e);
                Map resultMap = new HashMap();
                resultMap.put("id","26002");
                resultMap.put("status","0");
                resultMap.put("message","系统错误");
                session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
            }
        }else {
            Map resultMap = new HashMap();
            resultMap.put("id","26002");
            resultMap.put("status","0");
            resultMap.put("message","客户端未登录!");
            session.write(MyStringUtil.strTo16(JSONUtils.toJSONString(resultMap)));
        }
    }

    /**
     * 保存用户与session对应关系
     *
     */
    private void saveUserSession(Map userMap, IoSession session) {
        String username = (String) userMap.get("username");
        Map<Integer, Map> users = InitSorketParam.users;
        Set<Integer> userIds = users.keySet();
        for(Integer userId :userIds) {
            Map map = users.get(userId);
            String logging = (String) map.get("loginName");
            if(logging.equals(username)) {
                this.userMap.put(session.getId(),map);
            }
        }
    }

}
