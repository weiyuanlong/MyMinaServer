package com.aisafer.minasocket.api;

import com.aisafer.minasocket.service.DriverService;
import com.aisafer.minasocket.service.GPSInfoService;
import com.aisafer.minasocket.service.NewAlarmService;
import com.aisafer.minasocket.utils.MyStringUtil;
import com.aisafer.minasocket.utils.SpringContextUtil;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 推送数据的线程实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-07 10:00:02
 * @Modified By:
 */
public class SendMessageRunnable implements Runnable {

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(SendMessageRunnable.class);

    /** 操作报警事件的service层对象 */
    private NewAlarmService newAlarmService = (NewAlarmService) SpringContextUtil.getInstanceClass("newAlarmService");

    /** 操作GPS信息的service层对象 */
    private GPSInfoService gpsInfoService = (GPSInfoService) SpringContextUtil.getInstanceClass("gpsInfoService");

    /** 操作驾驶员信息的service层对象 */
    private DriverService driverService = (DriverService) SpringContextUtil.getInstanceClass("driverService");

    /** 用户连接对象 */
    private IoSession session;

    /** 向客户端发送数据时间间隔 */
    private Integer dataTimeInterval;

    /** 保存所有表查询的最后一条数据的ID和时间 */
    private Map<String,Map> dataIdAndTime = new HashMap<>();

    /**
     * 有参构造  只能传入session创建runnable实例
     *
     * @param session
     */
    public SendMessageRunnable (IoSession session,Integer dataTimeInterval) {
        this.session = session;
        this.dataTimeInterval = dataTimeInterval;

        // 初始化查询时间条件
        long now = System.currentTimeMillis();
        now -= (dataTimeInterval * 1000);
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String initTime = simpleDateFormat.format(date);

        // 报警事件
        Map idAndTime = dataIdAndTime.get("newAlarm");
        if(idAndTime == null ) {
            Map newAlaramMap = new HashMap();
            newAlaramMap.put("time",initTime);
            dataIdAndTime.put("newAlarm",newAlaramMap);
        }
        // 报警处理事件
        Map alarmRecord = dataIdAndTime.get("alarmRecord");
        if(alarmRecord == null ) {
            Map alaramRecordMap = new HashMap();
            alaramRecordMap.put("time",initTime);
            dataIdAndTime.put("alarmRecord",alaramRecordMap);
        }
        // 地理位置信息
        Map gps = dataIdAndTime.get("gps");
        if(gps == null ) {
            Map gpsMap = new HashMap();
            gpsMap.put("time",initTime);
            dataIdAndTime.put("gps",gpsMap);
        }

        // 初始化驾驶员查询时间
        Map driver = dataIdAndTime.get("driver");
        if(driver == null ) {
            Map driverMap = new HashMap();
            driverMap.put("time",initTime);
            dataIdAndTime.put("driver",driverMap);
        }
    }

    /**
     * 推送数据实现方法
     *
     */
    @Override
    public void run() {
        try {
            System.out.println(MyIoHandler.sessionMap.toString() + "=============" + MyIoHandler.sessionMap.size());
            if(MyIoHandler.sessionMap.containsKey(session.getId())) {
                if (session != null) {
                    Map map =  MyIoHandler.userMap.get(session.getId());
                    Integer userId = (Integer) map.get("userId");

                    // 推送报警事件数据
                    send_newAlarm(userId);
                    // 推送地理位置信息数据
                    send_gpsInfo(userId);
                    // 推送报警事件处理信息
                    send_alarmRecord(userId);
                    // 推送主驾驶信息
                    send_drivrrInfo(userId);
                }
            }else {
                new MyIoHandler().sessionClosed(session);
                return ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("推送数据实现方法",e);
        }
    }

    /**
     * 发送报警事件的方法
     *
     */
    private void send_newAlarm(Integer userId) {
        Map resultMap = new HashMap();
        try{
            resultMap.put("id","21002");
            resultMap.put("status","1");
            resultMap.put("message","数据加载成功");

            Integer lastId = null;
            String lastTime = "";
            Map idAndTime = dataIdAndTime.get("newAlarm");

            Object idObj = idAndTime.get("id");
            if(idObj instanceof Integer)
                lastId = (Integer) idObj;
            Object timeObj = idAndTime.get("time");
            lastTime = (String) timeObj;

            // 查询数据 保存最后ID和time
            Map newAlrms = newAlarmService.findNewAlarms(lastTime,lastId,dataTimeInterval,userId);

            Map newAlarm = (Map) newAlrms.get("newAlarm");
            Object id = newAlarm.get("id");
            Object time = newAlarm.get("time");
            if(id != null) {
                idAndTime.put("id",id);
            }
            if(time != null) {
                idAndTime.put("time",time);
            }
            send_data(resultMap, (List<Map>) newAlrms.get("data"));
        }catch (Exception e) {
            log.error("报警事件的方法出错",e);
            resultMap.put("id","21002");
            send_errData(resultMap);
        }

    }

    /**
     * 发送地址位置信息的方法
     *
     */
    private void send_gpsInfo(Integer userId) {
        Map resultMap = new HashMap();
        resultMap.put("id","23002");
        resultMap.put("status","1");
        resultMap.put("message","数据加载成功");

        Integer lastId = null;
        String lastTime = "";
        Map idAndTime = dataIdAndTime.get("gps");

        Object idObj = idAndTime.get("id");
        if(idObj instanceof Integer)
            lastId = (Integer) idObj;

        Object timeObj = idAndTime.get("time");
        lastTime = (String) timeObj;

        Map gpsInfos1 = gpsInfoService.findGpsInfos(lastTime, lastId, dataTimeInterval, userId);

        Map gps = (Map) gpsInfos1.get("gps");
        Object id = gps.get("id");
        Object time = gps.get("time");
        if(id != null) {
            idAndTime.put("id",id);
        }
        if(time != null) {
            idAndTime.put("time",time);
        }

        // dataIdAndTime.put("gps", (Map) gpsInfos1.get("gps"));
        send_data(resultMap, (List<Map>) gpsInfos1.get("data"));
    }

    /**
     * 发送报警事件处理的方法
     *
     */
    private void send_alarmRecord(Integer userId) {
        String lastTime = "";
        Map idAndTime = dataIdAndTime.get("alarmRecord");

        Object timeObj = idAndTime.get("time");
        lastTime = (String) timeObj;

        try{
            Map alarmRecords = newAlarmService.findAlarmRecords(lastTime, dataTimeInterval, userId);
            Map resultMap = new HashMap();
            resultMap.put("id","22002");
            resultMap.put("status","1");
            resultMap.put("message","数据加载成功");

            Map alarmRecord = (Map) alarmRecords.get("alarmRecord");

            Object time = alarmRecord.get("time");
            if(time != null) {
                idAndTime.put("time",time);
            }

            // dataIdAndTime.put("alarmRecord", alarmRecord1);
            send_data(resultMap, (List<Map>) alarmRecords.get("data"));
        }catch (Exception e) {
            log.error("报警事件处理的方法出错",e);
            Map resultMap = new HashMap();
            resultMap.put("id","22002");
            send_errData(resultMap);
        }

    }

    /**
     * 查询发送主驾驶信息
     *
     * @param userId
     */
    private void send_drivrrInfo(Integer userId) throws Exception {
        String lastTime = "";
        Map idAndTime = dataIdAndTime.get("driver");

        Object timeObj = idAndTime.get("time");
        lastTime = (String) timeObj;

        try{
            Map driverInfo = driverService.getDriverInfo(userId, lastTime);
            dataIdAndTime.put("driver", (Map) driverInfo.get("driver"));
            Map resultMap = new HashMap();
            resultMap.put("id","25002");
            resultMap.put("status","1");
            resultMap.put("message","数据加载成功");
            send_data(resultMap, (List<Map>) driverInfo.get("data"));
        }catch (Exception e) {
            log.error("推送主驾驶出错",e);
            Map resultMap = new HashMap();
            resultMap.put("id","25002");
            send_errData(resultMap);
        }

    }

    /**
     * 发送数据通用方法
     *
     * @param resultMap
     * @param maps
     */
    private void send_data(Map resultMap, List<Map> maps) {
        for(Map map : maps) {
            resultMap.put("data",map);
            String s = JSONUtils.toJSONString(resultMap);
            String s1 = MyStringUtil.strTo16(s);
            session.write(s1);
        }
    }

    /**
     * 查询失败发送数据方法
     *
     * @param resultMap
     */
    private void send_errData(Map resultMap) {
        resultMap.put("status","0");
        resultMap.put("message","数据加载失败");
        String s = JSONUtils.toJSONString(resultMap);
        String s1 = MyStringUtil.strTo16(s);
        session.write(s1);
    }

}
