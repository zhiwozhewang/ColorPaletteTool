package com.whatscolors.demo.api;

/**
 * Author:      wangwei
 * Date:        2019-12-03 23:22
 * Version:
 */

public class Contract {
    public String contractId; // 微信给的签约合同号

    public String contractUrl;// 可以调起微信的url

    public String productId; // IOS 会用到的 id，Android 端可以直接忽略
}