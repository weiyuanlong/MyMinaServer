package com.aisafer.minasocket.api;

import com.aisafer.minasocket.service.DepartmentService;
import com.aisafer.minasocket.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载组织缓存 和 车辆缓存
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 17:28:35
 * @Modified By:
 */
public class InitDepartmentRunnale implements Runnable {

    /** service层操作公司组织的对象 */
    private DepartmentService departmentService = (DepartmentService) SpringContextUtil.getInstanceClass("departmentService");

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(InitDepartmentRunnale.class);

    /**
     * 线程方法入口
     *
     */
    @Override
    public void run() {
        try{
            feshCachDepartment();
            log.info("初始化公司组织数据。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
            feshCachVehicle();
            log.info("初始化车辆数据。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
            feshCachVehicleType();
            log.info("初始化车辆类型数据。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
            feshCachDriver();
            log.info("初始化司机数据。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
            feshCachUser();
            log.info("初始化用户数据。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
        }catch (Exception e) {
            log.error("++++++++++++++++++++++++++++++++++++初始化缓存数据异常+++++++++++++++++++++++++++++++++++++++++++++++");
            log.error(e.getStackTrace().toString());
            e.printStackTrace();
        }

    }

    /**
     * 刷新公司组织数据的缓存
     *
     */
    private void feshCachDepartment() throws Exception {
        List<Map> departments = departmentService.findDepartmentAll();
        Map<Integer, String> departments1 = new HashMap<>();
        for(Map department : departments) {
            Integer depId = (Integer) department.get("depId");
            String name = String.valueOf(department.get("name"));
            departments1.put(depId,name);
        }
        InitSorketParam.departments = departments1;
    }

    /**
     * 刷新车辆数据的缓存
     *
     */
    private void feshCachVehicle() throws Exception {
        List<Map> vehicles = departmentService.findVehicleAll();
        Map<Integer, Map> vehicles1 = new HashMap<>();
        for(Map vehicle : vehicles) {
            Integer id = (Integer) vehicle.get("id");
            vehicles1.put(id,vehicle);
        }
        InitSorketParam.vehicles = vehicles1;
    }

    /**
     * 刷新车辆类型数据的缓存
     *
     */
    private void feshCachVehicleType() throws Exception {
        List<Map> vehicleTypes = departmentService.findVehicleTypeAll();
        Map<Integer, String> vehicles1 = new HashMap<>();
        for(Map vehicle : vehicleTypes) {
            Integer id = (Integer) vehicle.get("id");
            String typeName = String.valueOf(vehicle.get("name"));
            vehicles1.put(id,typeName);
        }
        InitSorketParam.vehicleTypes = vehicles1;
    }

    /**
     * 刷新司机数据的缓存
     *
     */
    private void feshCachDriver() throws Exception {
        List<Map> drivers = departmentService.findDriverAll();
        Map<Integer, Map> driverMap = new HashMap<>();
        for(Map driver : drivers) {
            driverMap.put((Integer) driver.get("id"),driver);
        }
        InitSorketParam.drivers = driverMap;
    }

    /**
     * 刷新用户数据的缓存
     *
     */
    private void feshCachUser() throws Exception {
        List<Map> users = departmentService.findUserAll();
        Map<Integer, Map> userMap = new HashMap<>();
        for(Map user : users) {
            userMap.put((Integer) user.get("userId"),user);
        }
        InitSorketParam.users = userMap;
    }

}
