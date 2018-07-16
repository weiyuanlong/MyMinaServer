package com.aisafer.minasocket.utils;

import com.alibaba.druid.support.json.JSONUtils;
import java.util.Map;

/**
 * 自定义字符串操作工具类
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-06 15:50:43
 * @Modified By:
 */
public class MyStringUtil {

    /**
     * 字符串转化成为16进制字符串
     * @param str
     * @return
     */
    public static String strTo16(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制转换成为string类型字符串
     *
     * @param s
     * @return
     */
    public static String str16ToStr(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 根据16进制数据 返回map
     *
     * @param str16
     * @return
     */
    public static Map getMapByStr16(String str16) {
        String stringToString = str16ToStr(str16);
        Map map = (Map) JSONUtils.parse(stringToString);
        return map;
    }

}
