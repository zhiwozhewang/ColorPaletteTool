package com.whatscolors.demo.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.LogUtils;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.Utils;

/**
 * APPLICATION
 */
public class BaseApplication extends Application {//ReaderApp

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        //使用副单位
//        AutoSizeConfig.getInstance().getUnitsManager()
//                .setSupportDP(false)
//                .setSupportSP(false)
//                .setSupportSubunits(Subunits.PT);
        //
        String str_v;
        if ("google".equals(getResources().getString(R.string.str_pay)))
            str_v = "gp";//+"-"+Utils.getVersionName(this);
        else {
            str_v = "dmv";//+"-"+Utils.getVersionName(this);
            initHa();
        }


        SPUtils.setSharedStringData(this, "useragent", "Android/" + Utils.getVersionName(baseApplication) + ", Whatscolors(" + Build.VERSION.RELEASE + ", " + Build.MODEL + ", " + Build.BRAND + "), " + str_v);
        LogUtils.logInit(MyConfig.DEBUG);

//        if (!MyConfig.DEBUG) {
//            LogToFile.init(getApplicationContext());
//            CrashHandler.getInstance().init(getApplicationContext());
//        }

    }

    public static Context getAppContext() {
        return baseApplication;
    }

    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 分包
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void initHa() {


//        AliHaConfig config = new AliHaConfig();
//        config.appKey = "333343660"; //配置项：appkey
//        config.appVersion = "1.9.0"; //配置项：应用的版本号
//        config.appSecret = "1c80ec576c484883b7a17de95310661d"; //配置项：appsecret
//        config.channel = "wc_test"; //配置项：应用的渠道号标记，自定义
//        config.userNick = null; //配置项：用户的昵称
//        config.application = this; //配置项：应用指针
//        config.context = getApplicationContext(); //配置项：应用上下文
//        config.isAliyunos = false; //配置项：是否为yunos
//        config.rsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRPm/Jbp8t6vxIle037pptTjoPsMYrux1yqVUg53pYLTc23DOSVOq8awgzlD6eIseamIxt+KmzDYugIUEIPcYgw1496yaUEbM1PXGjRHL2SVako8etXsh/G2KuTffN/yAgP4gd3b2iLxrdjBwmWaQRTx5GBu3AxpchpRgX0bhGswIDAQAB"; //配置项：tlog公钥
//        AliHaAdapter.getInstance().addPlugin(Plugin.tlog);
//        AliHaAdapter.getInstance().addPlugin(Plugin.crashreporter);
//        AliHaAdapter.getInstance().openDebug(true);
//        AliHaAdapter.getInstance().start(config);
//        TLogService.updateLogLevel(TLogLevel.VERBOSE); //配置项：控制台可拉取的日志级别
    }


}
