package com.whatscolors.demo.api;

/**
 * Author:      wangwei
 * Date:        2019-12-03 23:25
 * Version:
 */
public class GifBean {

    public String image_url;

    public int image_type;

    public int version;

    @Override
    public String toString() {
        return "GifBean{" +
                "image_url='" + image_url + '\'' +
                ", image_type=" + image_type +
                ", version=" + version +
                '}';
    }
}

