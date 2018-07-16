package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.api.MyIoHandler;
import com.aisafer.minasocket.mapper.DepartmentMapper;
import com.aisafer.minasocket.service.SimService;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 操作sim卡流量信息的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in
 * @Modified By:
 */
public class SimServiceImpl implements SimService {

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(SimServiceImpl.class);

    /** 操作公司组织的mapper对象 */
    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * @see SimService#getSimFlow(Object)
     *
     * @param simNo1
     * @return
     * @throws Exception
     */
    @Override
    public Map getSimFlow(Object simNo1) throws Exception {
        Map resultMap = new HashMap();
        resultMap.put("id","26002");

        if(simNo1 == null) {
            resultMap.put("status","0");
            resultMap.put("message","sim卡号为空!");
            resultMap.put("data",new HashMap<>());
            return resultMap;
        }

        String iccid = null;
        String simNo = String.valueOf(simNo1);

        try{
            long simNoL = Long.parseLong(simNo);
            simNoL = simNoL - 20000000000L;
            iccid = departmentMapper.findIccidBySimNo(simNoL);
            if(iccid == null || "".equals(iccid))
                throw new Exception();
        }catch (Exception e) {
            log.error("sim卡有误 sim卡号为：" + simNo);
            resultMap.put("status","0");
            resultMap.put("message","sim卡号有误!");
            resultMap.put("data",new HashMap<>());
            return resultMap;
        }

        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://120.24.171.163:8080/Aidriving/FlowInfo.html?iccid=" + iccid);
        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            Map parse = (Map) JSONUtils.parse(s);
            parse.put("simNo",simNo);

            String date = String.valueOf(parse.get("alterDate"));
            SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy K:m:s a",Locale.ENGLISH);
            Date parse1 = format.parse(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format2 = simpleDateFormat.format(parse1);

            parse.put("alterDate",format2);
            parse.remove("return_code");
            parse.remove("return_msg");

            resultMap.put("status","1");
            resultMap.put("message","数据查询成功!");
            resultMap.put("data",parse);
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            resultMap.put("status","0");
            resultMap.put("message","系统内部错误!");
            resultMap.put("data",new HashMap<>());
            return resultMap;
        }
    }

}
