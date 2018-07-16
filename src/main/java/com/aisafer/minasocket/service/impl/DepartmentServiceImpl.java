package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.mapper.DepartmentMapper;
import com.aisafer.minasocket.service.DepartmentService;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 操作公司组织的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 17:39:51
 * @Modified By:
 */
public class DepartmentServiceImpl implements DepartmentService {

    /** 操作公司组织的mapper对象 */
    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * @see DepartmentService#findDepartmentAll()
     *
     */
    @Override
    public List<Map> findDepartmentAll() throws Exception {
        return departmentMapper.findDepartmentAll();
    }

    /**
     * @see DepartmentService#findVehicleAll()
     *
     */
    @Override
    public List<Map> findVehicleAll() throws Exception {
        return departmentMapper.findVehicleAll();
    }

    /**
     * @see DepartmentService#findVehicleTypeAll()
     *
     */
    @Override
    public List<Map> findVehicleTypeAll() throws Exception {
        return departmentMapper.findVehicleTypeAll();
    }

    /**
     * @see DepartmentService#findDriverAll()
     *
     */
    @Override
    public List<Map> findDriverAll() throws Exception {
        return departmentMapper.findDriverAll();
    }

    /**
     * @see DepartmentService#findUserAll()
     *
     */
    @Override
    public List<Map> findUserAll() {
        return departmentMapper.findUserAll();
    }

}
