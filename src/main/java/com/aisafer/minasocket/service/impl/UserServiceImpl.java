package com.aisafer.minasocket.service.impl;

import com.aisafer.minasocket.mapper.UserMapper;
import com.aisafer.minasocket.service.UserService;
import org.apache.tomcat.util.security.MD5Encoder;
import sun.security.provider.MD5;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作用户的service层实现类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-19 09:50:33
 * @Modified By:
 */
public class UserServiceImpl implements UserService {

    /** 操作用户的mapper层对象 */
    @Resource
    private UserMapper userMapper;

    /**
     * @see UserService#userLogging(String, String)
     *
     */
    @Override
    public Map userLogging(String username, String password) throws Exception {
        Map resultMap = new HashMap();

        if(username == null || "".equals(username)) {
            resultMap.put("status","0");
            resultMap.put("message","用户名为空");
            return resultMap;
        }

        if(password == null || "".equals(password)) {
            resultMap.put("status","0");
            resultMap.put("message","密码为空");
            return resultMap;
        }
        // 根据用户名查询密码和推送数据时间间隔
        Map loggingMap = userMapper.findUserByUserName(username);

        // 查询数据为空
        if(loggingMap == null) {
            resultMap.put("status","0");
            resultMap.put("message","用户名不正确");
            return resultMap;
        }
        // 密码为空
        Object password1 = loggingMap.get("password");
        if(password1 == null) {
            resultMap.put("status","0");
            resultMap.put("message","用户名不正确");
            return resultMap;
        }
        String passWord = String.valueOf(password1);

        String s = MD5(password);
        if(!s.equals(passWord)) {
            resultMap.put("status","0");
            resultMap.put("message","密码不正确");
            return resultMap;
        }

        Integer timeInterval = (Integer) loggingMap.get("time_interval");
        if(timeInterval == null) {
            resultMap.put("status","0");
            resultMap.put("message","请配置推送数据事件间隔");
            return resultMap;
        }

        resultMap.put("timeInterval",timeInterval);
        resultMap.put("status","1");
        resultMap.put("message","登录成功");
        return resultMap;
    }

    /***
     * MD5加码 生成32位md5码
     */
    private String MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


}
