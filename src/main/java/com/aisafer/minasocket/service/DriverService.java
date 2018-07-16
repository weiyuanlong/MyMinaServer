package com.aisafer.minasocket.service;

import java.util.Map;

/**
 * 操作驾驶员的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-28 15:00:21
 * @Modified By:
 */
public interface DriverService {

    /**
     * 查询时间段内更新的主驾驶
     *
     * @param userId
     * @param updateTime
     * @return
     * @throws Exception
     */
    Map getDriverInfo(Integer userId, String updateTime) throws Exception;

}
