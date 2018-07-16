package com.aisafer.minasocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 操作公司组织的mapper接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 17:49:11
 * @Modified By:
 */
@Repository
public interface DepartmentMapper {

    /**
     * 查询所有公司组织
     *
     * @return
     */
    List<Map> findDepartmentAll();

    /**
     * 查询所有车辆
     *
     * @return
     */
    List<Map> findVehicleAll();

    /**
     * 查询所有车辆类型
     *
     * @return
     */
    List<Map> findVehicleTypeAll();

    /**
     * 查询所有司机
     *
     * @return
     */
    List<Map> findDriverAll();

    /**
     * 查询所有用户
     *
     * @return
     */
    List<Map> findUserAll();

    /**
     * 根据sim卡号查询ICCID
     *
     * @param simNo
     * @return
     */
    String findIccidBySimNo(@Param("simNo") Long simNo);

}
