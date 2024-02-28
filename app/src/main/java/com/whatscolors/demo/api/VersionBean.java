package com.whatscolors.demo.api;

/**
 * Author:      wangwei
 * Date:        2020/11/16 19:12
 * Version:
 */
public class VersionBean {
    public String download_url;
    public String version;

    @Override
    public String toString() {
        return "VersionBean{" +
                "download_url='" + download_url + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
