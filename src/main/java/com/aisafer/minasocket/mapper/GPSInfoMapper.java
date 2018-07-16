package com.aisafer.minasocket.mapper;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 操作GPS信息数据的mapper接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-13 11:35:31
 * @Modified By:
 */
@Repository
public interface GPSInfoMapper {

    /**
     * 查询gps数据
     *
     * @param paramMap
     * @return
     */
    List<Map> findGPSInfoParamMap(Map paramMap);

}
