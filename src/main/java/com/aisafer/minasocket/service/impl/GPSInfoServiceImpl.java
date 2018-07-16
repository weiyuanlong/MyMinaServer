package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.api.InitSorketParam;
import com.aisafer.minasocket.mapper.GPSInfoMapper;
import com.aisafer.minasocket.service.GPSInfoService;
import com.aisafer.minasocket.utils.ConfigValueUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 操作gps的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-13 11:38:15
 * @Modified By:
 */
public class GPSInfoServiceImpl implements GPSInfoService {

    /** 操作gps数据的mapper对象 */
    @Resource
    private GPSInfoMapper gpsInfoMapper;

    /**
     * 查询gps数据
     *
     * @param createTime
     * @param id
     * @return
     */
    @Override
    public Map findGpsInfos(String createTime, Integer id, Integer timeInterval, Integer userId) {
        List<Map> resultMap;
        // 查询条件参数
        Map paramMap = new HashMap();
        paramMap.put("id",id);
        paramMap.put("createTime",createTime);
        paramMap.put("userId",userId);
        String tableName;

        if(isLastDay(timeInterval)) {
            // 跨天 取前一天的表名和数据
            tableName = getTableName(true);
            paramMap.put("tableName",tableName);
            resultMap = gpsInfoMapper.findGPSInfoParamMap(paramMap);

            // 取当前的表名和所有数据
            tableName = getTableName(false);
            paramMap.put("tableName",tableName);
            paramMap.put("id",0);
            List<Map> resultMap2 = gpsInfoMapper.findGPSInfoParamMap(paramMap);
            for(Map map : resultMap2) {
                resultMap.set(resultMap.size(),map);
            }
        }else {
            // 未跨天或第一次获取数据
            tableName = getTableName(false);
            paramMap.put("tableName",tableName);
            resultMap = gpsInfoMapper.findGPSInfoParamMap(paramMap);
        }

        return setGpsInfoData(resultMap);
    }


    /**
     * 获取表名
     *
     * @param flag 是否跨天 true表示跨天
     * @return
     */
    private String getTableName(boolean flag) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String format = simpleDateFormat.format(date);
        String[] split = format.split("-");

        if(!flag)
            return "gpsinfo" + split[0] + split[1] + split[2];

        if(split[3].equals("00") && Integer.parseInt(split[4]) < 5) {
            // 0点的前五分钟 刚跨天
            if(split[2].equals("01")) {
                // 月份中的第一天 刚跨月
                if(split[1].equals("01")) {
                    // 年份中的第一个月 刚跨年
                    return "gpsinfo" + (Integer.parseInt(split[0])  - 1) + "12" + "31";
                }
                return "gpsinfo" + split[0] + (Integer.parseInt(split[1])  - 1) + getLastMonthDays(Integer.parseInt(split[1]));
            }
            return "gpsinfo" + split[0] + split[1] + (Integer.parseInt(split[2])  - 1);
        }
        return "gpsinfo" + split[0] + split[1] + split[2];
    }

    /**
     * 判断是否跨天
     *
     * @return 返回true表示跨天
     */
    private Boolean isLastDay(Integer timeInterval) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
        String format = simpleDateFormat.format(date);
        String[] split = format.split("-");

        if(split[0].equals("00")) {
            Integer min = Integer.parseInt(split[1]);
            Integer second = Integer.parseInt(split[2]);
            second = min + second;
            if(second > timeInterval)
                return false;
            else
                return true;
        }
        return false;
    }

    /**
     * 获取上个月最后一天
     *
     * @return
     */
    private String getLastMonthDays(Integer mohth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,mohth - 2);
        int MaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return String.valueOf(MaxDay);
    }

    /**
     * 查询GOS信息加入缓存
     *
     */
    private Map setGpsInfoData(List<Map> gpsInfos) {
        Integer gpsInfoId = null;
        String gpsInfoTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 遍历gps集合 修改数据信息
        for(int i = 0; i < gpsInfos.size(); i++) {
            String plateNo = (String) gpsInfos.get(i).get("plateNo");
            String vehicleTypeId = (String) gpsInfos.get(i).get("vehicleType");
            int typeId = Integer.parseInt(vehicleTypeId);

            String vehicleType = InitSorketParam.vehicleTypes.get(typeId);
            gpsInfos.get(i).put("vehicleType",vehicleType);

            // 更新gpsId和gpsInfoTime
            gpsInfoId = (Integer) gpsInfos.get(i).get("gpsId");
            Date createDate = (Date) gpsInfos.get(i).get("createDate");
            gpsInfoTime = simpleDateFormat.format(createDate);

            gpsInfos.get(i).remove("gpsId");
            gpsInfos.get(i).remove("createDate");
        }

        Map idAndTme = new HashMap();
        idAndTme.put("id",gpsInfoId);
        idAndTme.put("time",gpsInfoTime);

        Map resultMap = new HashMap();
        resultMap.put("gps",idAndTme);
        resultMap.put("data",gpsInfos);

        return resultMap;
    }

}
