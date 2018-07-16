package com.aisafer.minasocket.mapper;

import org.springframework.stereotype.Repository;
import java.util.Map;

/**
 * 操作直播的mapper
 *
 * @Author:weiyuanlong
 * @Date: Created in
 * @Modified By:
 */
@Repository
public interface SeedingMapper {

    /**
     * 根据用户登录名查询用户
     *
     * @param paramMap
     * @return
     */
    void insertSeeding(Map paramMap);

}
