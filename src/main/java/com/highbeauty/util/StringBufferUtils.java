package com.highbeauty.util;

/**
 * Copyright (C),2020
 * Author: jingxiaohu
 * Date: 2020/7/21 15:49
 * Description:
 */
public class StringBufferUtils {
    public static  String  replaceStr_Last(StringBuffer sb,String pattern){
        int index = sb.lastIndexOf(pattern);
        return  sb.substring(0,index);
    }
}
