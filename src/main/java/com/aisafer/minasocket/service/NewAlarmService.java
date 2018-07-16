package com.aisafer.minasocket.service;

import java.util.List;
import java.util.Map;

/**
 * 操作报警事件的service层接口
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-12 09:47:55
 * @Modified By:
 */
public interface NewAlarmService {

    /**
     * 查询报警事件
     *
     * @param createTime
     * @param id
     * @param dataTimeInterval
     * @return
     */
    Map findNewAlarms(String createTime, Integer id, Integer dataTimeInterval,Integer userId);

    /**
     * 根据车牌号、sim卡号、和创建时间 查询上传文件
     *
     * @param plateNo
     * @param simNo
     * @param newAlarmCreateTime
     * @return
     */
    List<String> findFileByPlateNoAndSimNo(String plateNo, String simNo, String newAlarmCreateTime);

    /**
     * 查询报警事件处理信息
     *
     * @param alarmRecordTime
     * @return
     */
    Map findAlarmRecords(String alarmRecordTime, Integer dataTimeInterval, Integer userId) throws Exception;

}
