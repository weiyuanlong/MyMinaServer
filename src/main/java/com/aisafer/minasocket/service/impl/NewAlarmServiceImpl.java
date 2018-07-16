package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.api.InitSorketParam;
import com.aisafer.minasocket.mapper.NewAlarmMapper;
import com.aisafer.minasocket.service.NewAlarmService;
import com.aisafer.minasocket.utils.ConfigValueUtil;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 操作报警事件的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-12 10:00:55
 * @Modified By:
 */
public class NewAlarmServiceImpl implements NewAlarmService {

    /** 操作报警事件的mapper对象 */
    @Resource
    private NewAlarmMapper newAlarmMapper;

    /**
     * @see NewAlarmService#findNewAlarms(String, Integer, Integer, Integer)
     *
     */
    @Override
    public Map findNewAlarms(String createTime,Integer id, Integer dataTimeInterval, Integer userId) {
        List<Map> resultMap;
        Map paramMap = new HashMap();
        Boolean flag = isLastMonth(dataTimeInterval);
        paramMap.put("id",id);
        paramMap.put("createTime",createTime);
        paramMap.put("userId",userId);

        // 第一次初始化数据
        if(id == null)
            flag = false;

        // 获取本月表名
        String tableName = getTableName(false);
        paramMap.put("tableName",tableName);

        if(flag) {
            // 刚跨月
            // 获取本月数据 获取所有
            paramMap.put("id",0);
            resultMap = newAlarmMapper.findAlarmByIdOrTime(paramMap);

            // 获取上月数据
            String lastTableName = getTableName(true);
            paramMap.put("tableName",lastTableName);
            List<Map> lastNewAlarms = newAlarmMapper.findAlarmByIdOrTime(paramMap);

            for(Map map : lastNewAlarms) {
                resultMap.add(resultMap.size(),map);
            }
        }else {
            // 未跨月
            resultMap = newAlarmMapper.findAlarmByIdOrTime(paramMap);
        }
        return setNewAlarms(resultMap);
    }

    /**
     * @see NewAlarmService#findFileByPlateNoAndSimNo(String, String, String)
     *
     */
    @Override
    public List<String> findFileByPlateNoAndSimNo(String plateNo, String simNo, String newAlarmCreateTime) {

        return newAlarmMapper.findFileByPlateNoAndSimNo(plateNo,simNo,newAlarmCreateTime);
    }

    /**
     * @see NewAlarmService#findAlarmRecords(String, Integer, Integer)
     *
     */
    @Override
    public Map findAlarmRecords(String alarmRecordTime, Integer dataTimeInterval, Integer userId) throws Exception {
        String tableName;
        List<Map> resultMap;
        Boolean flag = isLastMonth(dataTimeInterval);

        if(flag) {
            // 获取上月数据
            tableName = getTableName(true);
            resultMap = newAlarmMapper.findAlarmRecords(alarmRecordTime,tableName,userId);

            // 获取本月表名 和 时间条件
            tableName = getTableName(false);
            SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyy-MM-dd");
            alarmRecordTime = yyyyMM.format(new Date());
            List<Map> resultMap1 = newAlarmMapper.findAlarmRecords(alarmRecordTime,tableName,userId);

            for(Map map : resultMap1) {
                resultMap.add(resultMap.size(),map);
            }
        }else {
            tableName = getTableName(false);
            resultMap = newAlarmMapper.findAlarmRecords(alarmRecordTime,tableName,userId);
        }

        return setAlarmRecordData(resultMap);
    }

    /**
     * 获取表名
     *
     * @param flag 是否取上月的表名
     * @return
     */
    private String getTableName(Boolean flag) {
        Date now = new Date();

        if(flag) {
            // 跨月
            SimpleDateFormat monthFormart = new SimpleDateFormat("MM");
            Integer month = Integer.parseInt(monthFormart.format(now));
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            if(month <= 1) {
                // 取去年最后一个月的值
                Integer lastYear = Integer.parseInt(yearFormat.format(now)) - 1;
                System.out.println("表名为   " + "newalarm" + lastYear + "12");
                return "newalarm" + lastYear + "12";
            }else {
                // 取上个月的值
                System.out.println("表名为    " + "newalarm" + yearFormat.format(now) + (month-1));
                return "newalarm" + yearFormat.format(now) + (month-1);
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        String yearAndMonth = simpleDateFormat.format(now);
        String tableName = "newalarm" + yearAndMonth;
        return tableName;
    }

    /**
     * 判断是否刚跨月
     * 是 返回true
     * 不是 返回false
     *
     * @return
     */
    private Boolean isLastMonth(Integer dataTimeInterval) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-HH-mm-ss");
        String format = simpleDateFormat.format(date);

        String[] split = format.split("-");
        if(!split[0].equals("01"))
            // 不是月份的第一天
            return false;

        if(!split[1].equals("00"))
            // 不是一天中的凌晨
            return false;

        Integer min = Integer.parseInt(split[2]);
        Integer second = Integer.parseInt(split[3]);
        second = min + second;

        if(second > dataTimeInterval)
            return false;

        return true;
    }

    /**
     * 查询出报警事件的数据
     *
     */
    public Map setNewAlarms(List<Map> newAlarms) {
        Integer newAlarmId = null;
        String newAlarmCreateTime = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(int i = 0; i < newAlarms.size(); i++) {

            // 将最后一条有效数据的创建时间和ID保存
            newAlarmId = (Integer) newAlarms.get(i).get("id");
            Date createDate = (Date) newAlarms.get(i).get("createDate");
            newAlarmCreateTime = simpleDateFormat.format(createDate);

            Integer vehicleId = (Integer) newAlarms.get(i).get("vehicleId");
            Map vehicle = InitSorketParam.vehicles.get(vehicleId);
            if(vehicle == null || vehicle.size() < 1)
                continue;

            // 根据车牌号和sim卡号查询图片
            Object plateNo1 = vehicle.get("plateNo");
            if(plateNo1 != null) {
                String plateNo = String.valueOf(plateNo1);
                Object simNo1 = vehicle.get("simNo");
                String simNo = null;
                if(simNo1 != null) {
                    simNo = String.valueOf(simNo1);
                }
                List<String> files = findFileByPlateNoAndSimNo(plateNo,simNo,newAlarmCreateTime);
                String filesStr = "[";

                for(int j = 0; j < files.size(); j++) {
                    if(j == files.size()-1)
                        filesStr += "http://jt808files.oss-cn-shenzhen.aliyuncs.com/vehiclePicture/" + files.get(j);
                    else
                        filesStr += "http://jt808files.oss-cn-shenzhen.aliyuncs.com/vehiclePicture/" + files.get(j) + ",";
                }
                // 将报警文件路径保存
                newAlarms.get(i).put("alarmFiles",filesStr + "]");
            }

            // 移除掉不需要传递的字段
            newAlarms.get(i).remove("vehicleId");
            newAlarms.get(i).remove("createDate");
            newAlarms.get(i).remove("driverId");
            newAlarms.get(i).remove("simNo");
        }
        Map lastMap = new HashMap();
        lastMap.put("id",newAlarmId);
        lastMap.put("time",newAlarmCreateTime);

        Map resultMap = new HashMap();
        resultMap.put("newAlarm",lastMap);
        resultMap.put("data",newAlarms);

        return  resultMap;
    }

    /**
     * 加载报警事件处理信息数据
     *
     */
    private Map setAlarmRecordData(List<Map> alarmRecords) throws Exception {
        Map result = new HashMap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String alarmRecordTime = null;

        for(int i = 0; i < alarmRecords.size(); i++) {
            // 更新最近处理时间
            Date actiontime = (Date) alarmRecords.get(i).get("actiontime");
            simpleDateFormat.format(actiontime);

            // 获取司机相关信息
            Integer driverId = (Integer) alarmRecords.get(i).get("driverId");
            Map driver = InitSorketParam.drivers.get(driverId);
            if(driver != null && driver.keySet().size() > 0) {
                alarmRecords.get(i).put("driverName",String.valueOf(driver.get("driverName")));
                alarmRecords.get(i).put("mobilePhone ",String.valueOf(driver.get("telephone")));
                alarmRecords.get(i).put("identityCard ",String.valueOf(driver.get("identityCard")));
            }

            // 获取处理人用户名
            Integer userId = (Integer) alarmRecords.get(i).get("userId");
            if(userId != null) {
                Map user = InitSorketParam.users.get(userId);
                alarmRecords.get(i).put("loginname",String.valueOf(user.get("loginName")));
            }

            alarmRecords.get(i).remove("driverId");
            alarmRecords.get(i).remove("vehicleId");
            alarmRecords.get(i).remove("userId");
        }

        Map map = new HashMap();
        map.put("time",alarmRecordTime);

        result.put("alarmRecord",map);
        result.put("data",alarmRecords);
        return result;
    }

}
