package com.aisafer.minasocket.api;

import com.aisafer.minasocket.utils.MyStringUtil;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端进行消息过滤  心跳检测使用
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-05 13:25:55
 * @Modified By:
 */
public class MyKeepAliveMessageFactory implements KeepAliveMessageFactory {

    /**
     * 进行消息过滤  如果返回true  messageReceived不会收到消息
     *
     * @param session
     * @param message
     * @return
     */
    @Override
    public boolean isRequest(IoSession session, Object message) {

        return false;
    }

    /**
     * 判断心跳检测客户端回应
     *
     * @param session
     * @param message
     * @return
     */
    @Override
    public boolean isResponse(IoSession session, Object message) {
        Map paramMap = MyStringUtil.getMapByStr16(message.toString());
        // System.out.println(paramMap.toString());
        if(paramMap.containsKey("id") && paramMap.get("id").toString().equals("11001")) {
            // 处理客户端心跳检查结果
            // 客户端响应心跳检测 将记录数重置
            MyIoHandler.hardSendNum.put(session.getId(),0);

            if(!MyIoHandler.sessionMap.containsKey(session.getId())) {
                try {
                    // 重新开启线程发送消息  将session加入map中
                    new MyIoHandler().sessionOpened(session);
                    MyIoHandler.sessionMap.put(session.getId(),session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
        return false;
    }

    /**
     * 获取心跳请求数据
     *
     * @param session
     * @return
     */
    @Override
    public Object getRequest(IoSession session) {
        Map<Long, IoSession> sessionMap = MyIoHandler.sessionMap;
        IoSession ioSession = sessionMap.get(session.getId());
        if(ioSession == null)
            return null;

        Map sendMap = new HashMap();
        sendMap.put("id","11002");
        sendMap.put("live","in live");

        String sendJson = JSONUtils.toJSONString(sendMap);
        String send16 = MyStringUtil.strTo16(sendJson);

        Integer sendNum = MyIoHandler.hardSendNum.get(session.getId());
        if(sendNum == null)
            // 首次发送心跳检测  记录发送次数为1
            MyIoHandler.hardSendNum.put(session.getId(),1);
        else
            // 每发送一次心跳检测 记录+1
            MyIoHandler.hardSendNum.put(session.getId(),++sendNum);

        return send16;
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        System.out.println("getResponse");
        return null;
    }

}
