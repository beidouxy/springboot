package com.beidou.springsecurity.utils.redis;

import java.util.UUID;

/**
 * Redis所有Keys
 *
 * @author Centling Technologies
 * @email centling@centling.com
 * @date 2017-07-18 19:51
 */
public class RedisKeys {
    /**
     * 生成系统配置key
     * @param key
     * @return
     */
    public static String getSysConfigKey(String key){
        return "springsecurity-sys:config:" + key;
    }

    /**
     *  生成存放用户token的redis key
     * @param key
     * @return
     */
    public static String getUserAppTokenKey(String key){
        return "springsecurity-userapptoken:"+key;
    }

    /**
     * 生成角色组织机构的key
     * @param key
     * @return
     */
    public static String getRoleOrginization(String key){
        return "springsecurity-roleorginization:"+key;
    }
    /**
     *  生成存放用户验证码的redis key
     *
     * @return
     */
    public static String getRedisKey(){
        String uuid = UUID.randomUUID().toString();
        return "springsecurity-rediskey:"+uuid;
    }

    /**
     *  生成下载的redis key
     *
     * @return
     */
    public static String getDownloadKey(){
        String uuid = UUID.randomUUID().toString();
        return "springsecurity-downloadKey:"+uuid;
    }
    /**
     *  生成编辑文档的redis key
     *
     * @return
     */
    public static String getOfficeKey(){
        String uuid = UUID.randomUUID().toString();
        return "springsecurity-officeKey:"+uuid;
    }

    /** 生成存放微信用户token的key */
    public static String getWechatUserToken(String key){
        return "springsecurity-wechatUserToken:"+key;
    }

}