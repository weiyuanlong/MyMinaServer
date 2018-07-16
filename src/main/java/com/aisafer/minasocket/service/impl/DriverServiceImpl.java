package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.mapper.DriverMapper;
import com.aisafer.minasocket.service.DriverService;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作司机的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-28 15:02:38
 * @Modified By:
 */
public class DriverServiceImpl implements DriverService {

    /** 操作驾驶员的mapper对象 */
    @Resource
    private DriverMapper driverMapper;

    /**
     * @see DriverService#getDriverInfo(Integer, String)
     *
     */
    @Override
    public Map getDriverInfo(Integer userId, String updateTime) throws Exception {
        Map result = new HashMap();
        Map timeAndId = new HashMap();

        List<Map> drivers = driverMapper.findDriverByMap(userId, updateTime);

        if(drivers == null || drivers.size() < 1) {
            timeAndId.put("time",updateTime);
        }else {
            Map map = drivers.get(drivers.size() - 1);
            Date newUdateTime = (Date) map.get("timeStamp");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(newUdateTime);
            timeAndId.put("time",format);
        }

        result.put("driver",timeAndId);
        result.put("data",drivers);
        return result;
    }

}
