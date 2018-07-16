package com.aisafer.minasocket.service;

import java.util.Map;

/**
 * 操作sim卡信息的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-27 16:07:14
 * @Modified By:
 */
public interface SimService {

    /**
     * 根据sim卡号查询流量
     *
     * @param simNo
     * @return
     * @throws Exception
     */
    Map getSimFlow(Object simNo) throws Exception;

}
