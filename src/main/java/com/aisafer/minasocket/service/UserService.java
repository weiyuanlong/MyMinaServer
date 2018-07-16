package com.aisafer.minasocket.service;

import java.util.Map;

/**
 * 操作用户的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-19 09:44:18
 * @Modified By:
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    Map userLogging(String username, String password) throws Exception;

}
