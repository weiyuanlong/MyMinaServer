package com.aisafer.minasocket.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 操作报警事件的mapper
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 15:29:08
 * @Modified By:
 */
@Repository
public interface NewAlarmMapper {

    /**
     * 根据条件查询报警事件
     *
     * @param paramMap
     * @return
     */
    List<Map> findAlarmByIdOrTime(Map paramMap);

    /**
     * 根据车牌号 sim卡号  创建时间 查询上传文件名称
     *
     * @param plateNo
     * @param simNo
     * @param newAlarmCreateTime
     * @return
     */
    List<String> findFileByPlateNoAndSimNo(@Param("plateNo") String plateNo, @Param("simNo") String simNo, @Param("newAlarmCreateTime") String newAlarmCreateTime);

    /**
     * 查询报警事件处理信息数据
     *
     * @param alarmRecordTime
     * @param tableName
     * @return
     */
    List<Map> findAlarmRecords(@Param("alarmRecordTime") String alarmRecordTime, @Param("tableName") String tableName, @Param("userId") Integer userId);

}
