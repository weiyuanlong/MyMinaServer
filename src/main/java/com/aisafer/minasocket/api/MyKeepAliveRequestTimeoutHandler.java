package com.aisafer.minasocket.api;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author:weiyuanlong
 * @Date: Created in
 * @Modified By:
 */
public class MyKeepAliveRequestTimeoutHandler implements KeepAliveRequestTimeoutHandler {

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(MyKeepAliveRequestTimeoutHandler.class);

    /**
     * 心跳检测失败回调方法
     *
     * @param keepAliveFilter
     * @param ioSession
     * @throws Exception
     */
    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
        Integer sendNum = MyIoHandler.hardSendNum.get(ioSession.getId());
        if(sendNum != null && sendNum >= 3){
            log.error("客户端挂了!  客户端地址为：  " + ioSession.getRemoteAddress());

            // 关闭此客户端
            new MyIoHandler().sessionClosed(ioSession);
        }
    }

}
