package com.whatscolors.demo;

import com.android.billingclient.api.BillingClient;
import com.whatscolors.demo.takephoto.BuildConfig;

public class MyConfig {

    public static boolean DEBUG = BuildConfig.DEBUG;
    //    String[] ids_google = {"onetime_v1", "3m_services", "1m_sub_v1"};//  一次性买断,    按月购买,按年购买
    public static String[] ids_google = {"onetime_v1", "quarter_v2", "year_v2"};//  一次性买断,    按月购买,按年购买
    //        String[] ids_google = {"onetime_v1", "quarter_v1", "year_v1"};//  一次性买断,    按月订阅,按年订阅
    public static String[] ids_wechat = {"onetime_v1", "quarter_v2", "year_v2"};//  一次性买断,    按月订阅,按年订阅

    public static String[] types = {BillingClient.SkuType.INAPP, BillingClient.SkuType.SUBS};//  一次性买断,    按月订阅,按年订阅
//    id: wx788baac4f53e190e
//    secret: 92ff54530cf2e6eee8e973b7067e8b33

    public static String APP_ID = "wx788baac4f53e190e";
    public static String WECHAT_PACKAGE = "Sign=WXPay";
    public static int PAY_STATUS_SUCCESS = 0;
    public static int SEND_STATUS_FAILED = 2;

    public static int USER_STATUS_SUCCESS = 1;
    //    user_key 报错
    public static int USER_KEY_WRONG = 121;
    //    user_key 已过期
    public static int USER_KEY_OUTTIME = 123;
    //      账号在其他设备登录
    public static int USER_KEY_LOGIN_ABNORMAL = 136;
    //      您已达到设备的最大数量限制
    public static int USER_KEY_LOGIN_LIMIT = 129;
    //    密码已修改
    public static int USER_KEY_PASSWORD_CHANGED = 131;
    //'账号在新设备登录, 需要使用一次性密码确认登录操作'
    public static int USER_KEY_PASSWORD_ONCE_ONLY = 132;

    public static String[] str_market = {"com.android.vending", "com.tencent.android.qqdownloader", "com.qihoo.appstore", "com.huawei.appmarket"};// "com.xiaomi.market"
    public static String gifname = "startgif.gif";
    public static int GIF_VERSION = 1;


}
