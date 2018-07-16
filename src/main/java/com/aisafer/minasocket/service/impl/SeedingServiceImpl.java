package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.api.InitSorketParam;
import com.aisafer.minasocket.api.MyIoHandler;
import com.aisafer.minasocket.mapper.SeedingMapper;
import com.aisafer.minasocket.service.SeedingService;
import org.apache.mina.core.session.IoSession;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 操作直播的servie层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-27 13:37:42
 * @Modified By:
 */
@Transactional
public class SeedingServiceImpl implements SeedingService {

    /** 操作直播的mapper对象 */
    @Resource
    private SeedingMapper seedingMapper;

    /**
     * @see SeedingService#seeding(Map, IoSession)
     *
     */
    @Override
    public Map seeding(Map paramMap, IoSession sessions) throws Exception {
        Map redsultMap = new HashMap();
        redsultMap.put("id","24002");

        Object simNo = paramMap.get("simNo");
        Object nchan = paramMap.get("nchan");
        if(simNo == null || "".equals(simNo)) {
            redsultMap.put("status","0");
            redsultMap.put("message","simNo为空!");
            return redsultMap;
        }

        if(nchan == null) {
            redsultMap.put("status","0");
            redsultMap.put("message","通道号为空!");
            return redsultMap;
        }

        if(nchan instanceof Integer) {
            Integer nchanInt = (Integer) nchan;
            if(nchanInt < 1 || nchanInt > 6) {
                redsultMap.put("status","0");
                redsultMap.put("message","通道号有误!");
                return redsultMap;
            }else {
                // 插入直播信息
                insertSeeding(String.valueOf(simNo),nchanInt,sessions);
                redsultMap.put("status","1");
                redsultMap.put("message","添加直播成功!");
            }
        }else {
            redsultMap.put("status","0");
            redsultMap.put("message","通道号类型有误!");
            return redsultMap;
        }

        return redsultMap;
    }

    /**
     * 插入一条直播信息
     *
     * @param simNo
     * @param nchan
     * @param session
     */
    private void insertSeeding(String simNo, Integer nchan, IoSession session) throws Exception {
        Map<Long, Map> userMap = MyIoHandler.userMap;
        Map map = userMap.get(session.getId());

        String owner = "";
        Object name = map.get("name");
        if(name != null)
            owner = (String) name;
        // 所属组织
        map.put("owner",owner);
        // 组装通道号
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 1; i <= 6; i++) {
            if(i == nchan) {
                if(i == 1) {
                    stringBuffer.append("1");
                }else {
                    stringBuffer.append(";1");
                }
            }else {
                if(i == 1) {
                    stringBuffer.append("0");
                }else {
                    stringBuffer.append(";0");
                }
            }
        }
        // 通道号
        map.put("cmdData",stringBuffer.toString());

        Integer vehicleId = null;
        String plateNo = null;

        Map<Integer, Map> vehicles = InitSorketParam.vehicles;
        Set<Integer> vehicleIds = vehicles.keySet();
        for(Integer key : vehicleIds) {
            Map vehicle = vehicles.get(key);
            Object simNo1 = vehicle.get("simNo");
            if(simNo1 != null) {
                if(simNo1.equals(simNo)) {
                    vehicleId = (Integer) vehicle.get("id");
                    plateNo = (String) vehicle.get("plateNo");
                    break;
                }
            }
        }
        // 车辆ID和车牌号
        map.put("vehicleId",vehicleId);
        map.put("plateNo",plateNo);
        map.put("createDate",new Date());
        map.put("deleted",0);
        map.put("cmdType",36865);
        map.put("simNo",simNo);
        map.put("status","New");
        map.put("sn",0);
        map.put("tenantId",0);

        seedingMapper.insertSeeding(map);
    }

}
