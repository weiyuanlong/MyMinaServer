package com.aisafer.minasocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 操作司机的mapper
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-28 13:48:30
 * @Modified By:
 */
@Repository
public interface DriverMapper {

    /**
     * 查询最近更新的主驾驶员
     *
     * @param userId
     * @param updateTime
     * @return
     */
    List<Map> findDriverByMap(@Param("userId") Integer userId, @Param("updateTime") String updateTime);

}
