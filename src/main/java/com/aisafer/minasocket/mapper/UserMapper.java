package com.aisafer.minasocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 操作用户的mapper
 *
 * @Author:weiyuanlong
 * @Date: Created in
 * @Modified By:
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户登录名查询用户
     *
     * @param username
     * @return
     */
    Map findUserByUserName(@Param("username") String username);

}
