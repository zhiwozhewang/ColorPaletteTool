package com.whatscolors.demo.utils;

/**
 * 权限请求结果回调接口
 */
public interface RequestPermissionCallBack {
    /**
     * 同意授权
     */
    void granted();

    /**
     * 取消授权
     */
    void denied();
}
