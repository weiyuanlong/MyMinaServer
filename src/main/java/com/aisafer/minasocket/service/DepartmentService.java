package com.aisafer.minasocket.service;

import java.util.List;
import java.util.Map;

/**
 * 操作公司组织的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 17:39:07
 * @Modified By:
 */
public interface DepartmentService {

    /**
     * 查询所有组织
     *
     * @return
     */
    List<Map> findDepartmentAll() throws Exception;

    /**
     * 查询所有车辆
     *
     * @return
     */
    List<Map> findVehicleAll() throws Exception;

    /**
     * 查询所有车辆类型
     *
     * @return
     */
    List<Map> findVehicleTypeAll() throws Exception;

    /**
     * 查询所有司机
     *
     * @return
     */
    List<Map> findDriverAll() throws Exception;

    /**
     * 查询所有用户
     *
     * @return
     */
    List<Map> findUserAll();

}
