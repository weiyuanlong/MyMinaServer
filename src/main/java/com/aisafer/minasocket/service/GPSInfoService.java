package com.aisafer.minasocket.service;

import java.util.List;
import java.util.Map;

/**
 * 操作GPS信息数据的service接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-13 11:36:59
 * @Modified By:
 */
public interface GPSInfoService {

    /**
     * 根据条件查询gps信息
     *
     * @param createTime
     * @param id
     * @return
     */
    Map findGpsInfos(String createTime, Integer id, Integer timeInterval, Integer userId);

}
