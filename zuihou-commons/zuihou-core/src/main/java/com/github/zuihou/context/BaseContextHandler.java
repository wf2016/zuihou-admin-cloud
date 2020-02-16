package com.github.zuihou.context;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 获取当前域中的 用户id appid 用户昵称
 * 注意： appid 通过token解析，  用户id 和 用户昵称必须在前端 通过请求头的方法传入。 否则这里无法获取
 *
 * @author zuihou
 * @createTime 2017-12-13 16:52
 */
public class BaseContextHandler {
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key, Long value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "0" : String.valueOf(value));
    }

    public static void set(String key, String value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "" : value);
    }

    public static void set(String key, Boolean value) {
        Map<String, String> map = getLocalMap();
        map.put(key, value == null ? "false" : value.toString());
    }


    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }


    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, "");
    }


//    public static Boolean isBoot() {
//        Object value = get(BaseContextConstants.IS_BOOT);
//        return Convert.toBool(value);
//    }

    /**
     * 账号id
     *
     * @param val
     */
//    public static void setBoot(Boolean val) {
//        set(BaseContextConstants.IS_BOOT, val);
//    }

    /**
     * 账号id
     *
     * @return
     */
    public static Long getUserId() {
        Object value = get(BaseContextConstants.JWT_KEY_USER_ID);
        return Convert.toLong(value, 0L);
    }

    /**
     * 账号id
     *
     * @param userId
     */
    public static void setUserId(Long userId) {
        set(BaseContextConstants.JWT_KEY_USER_ID, userId);
    }

    public static void setUserId(String userId) {
        setUserId(Convert.toLong(userId, 0L));
    }

    /**
     * 账号表中的name
     *
     * @return
     */
    public static String getAccount() {
        Object value = get(BaseContextConstants.JWT_KEY_ACCOUNT);
        return returnObjectValue(value);
    }

    /**
     * 账号表中的name
     *
     * @param name
     */
    public static void setAccount(String name) {
        set(BaseContextConstants.JWT_KEY_ACCOUNT, name);
    }


    /**
     * 登录的账号
     *
     * @return
     */
    public static String getName() {
        Object value = get(BaseContextConstants.JWT_KEY_NAME);
        return returnObjectValue(value);
    }

    /**
     * 登录的账号
     *
     * @param account
     */
    public static void setName(String account) {
        set(BaseContextConstants.JWT_KEY_NAME, account);
    }

    /**
     * 获取用户token
     *
     * @return
     */
    public static String getToken() {
        Object value = get(BaseContextConstants.TOKEN_NAME);
        return Convert.toStr(value);
    }

    public static void setToken(String token) {
        set(BaseContextConstants.TOKEN_NAME, token);
    }

    @Deprecated
    public static Long getOrgId() {
        Object value = get(BaseContextConstants.JWT_KEY_ORG_ID);
        return Convert.toLong(value, 0L);
    }

    @Deprecated
    public static void setOrgId(String val) {
        set(BaseContextConstants.JWT_KEY_ORG_ID, val);
    }

    @Deprecated
    public static void setOrgId(Long val) {
        set(BaseContextConstants.JWT_KEY_ORG_ID, val);
    }

    @Deprecated
    public static Long getStationId() {
        Object value = get(BaseContextConstants.JWT_KEY_STATION_ID);
        return Convert.toLong(value, 0L);
    }

    @Deprecated
    public static void setStationId(String val) {
        set(BaseContextConstants.JWT_KEY_STATION_ID, val);
    }

    @Deprecated
    public static void setStationId(Long val) {
        set(BaseContextConstants.JWT_KEY_STATION_ID, val);
    }

    public static String getTenant() {
        Object value = get(BaseContextConstants.TENANT);
        return Convert.toStr(value);
    }

    public static void setTenant(String val) {
        set(BaseContextConstants.TENANT, val);
    }

    public static String getDatabase(String tenant) {
        Object value = get(BaseContextConstants.DATABASE_NAME);
        String objectValue = Convert.toStr(value);
        return objectValue + StrUtil.UNDERLINE + tenant;
    }

    public static String getDatabase() {
        Object value = get(BaseContextConstants.DATABASE_NAME);
        return Convert.toStr(value);
    }

    public static void setDatabase(String val) {
        set(BaseContextConstants.DATABASE_NAME, val);
    }

    public static String getGrayVersion() {
        Object value = get(BaseContextConstants.GRAY_VERSION);
        return Convert.toStr(value);
    }

    public static void setGrayVersion(String val) {
        set(BaseContextConstants.GRAY_VERSION, val);
    }


    private static String returnObjectValue(Object value) {
        return value == null ? "" : value.toString();
    }

    public static void remove() {
        if (THREAD_LOCAL != null) {
            THREAD_LOCAL.remove();
        }
    }

}
