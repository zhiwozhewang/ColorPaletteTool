package com.whatscolors.demo.utils;

import android.app.Activity;

import com.tencent.mm.opensdk.modelbiz.OpenWebview;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.whatscolors.demo.MyConfig;

import java.util.HashMap;

/**
 * Author:      wangwei
 * Date:        2019-12-03 19:09
 * Version:     1.0
 */
public class WeChatPayUtil {
    private static IWXAPI sWXAPI;

    /**
     * 微信代扣
     *
     * @param pActivity
     * @param url
     * @return
     * @throws Exception
     */
    public static boolean pay(Activity pActivity, String url) throws Exception {
        if (sWXAPI == null) {
            sWXAPI = WXAPIFactory.createWXAPI(pActivity, MyConfig.APP_ID);
        }
//        if (微信未安装) {
//            ToastUitl.showShort( R.string.no_wechat);
//            return false;
//        }
        toWechatContractPay(url);
        return true;
    }

    private static void toWechatContractPay(String url) {
        OpenWebview.Req req = new OpenWebview.Req();
        req.url = url;
        sWXAPI.sendReq(req);
    }

    //签约
    public static void toSigning(Activity context, String url) {

        WXOpenBusinessWebview.Req req = new WXOpenBusinessWebview.Req();
        req.businessType = 12;//固定值
        HashMap queryInfo = new HashMap<>();
        queryInfo.put("pre_entrustweb_id", "5778aad8fbd11b3846978993fedf2cb8d6c8f86ea809389b2bc30c4867d563db1566876267UpxT91EYRKY9nltAsZzXixCkFIGYnV2V");
        req.queryInfo = queryInfo;
        sWXAPI.sendReq(req);
    }

}
