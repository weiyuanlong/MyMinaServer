package com.aisafer.minasocket.service;

import org.apache.mina.core.session.IoSession;
import java.util.Map;

/**
 * 操作直播的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-27 13:37:08
 * @Modified By:
 */
public interface SeedingService {

    /**
     * 用户直播操作
     *
     * @param paramMap
     * @return
     */
    Map seeding(Map paramMap, IoSession session) throws Exception;

}
