package com.whatscolors.demo.api;

/**
 * Author:      wangwei
 * Date:        2019-12-03 23:25
 * Version:
 */
public class OrderStatus {
    public String status;//granted, ungranted, pending, unsubscribed

    public long singedAt;//签约成功的时间（这个只有在 granted 状态时才有效）

    public String contractType;//wechat, iap

    public OrderStatusWithhold withhold;


    public static class OrderStatusWithhold {
        public String status;//<"created", "pending", "succeeded", "failed">

        public String errorMessage;

    }
}

