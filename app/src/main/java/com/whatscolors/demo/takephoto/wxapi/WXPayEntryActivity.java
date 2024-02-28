package com.whatscolors.demo.takephoto.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.utils.LogUtils;
import com.whatscolors.demo.utils.RxBus;

/**
 * Author:      wangwei
 * Date:        2019-12-02 23:10
 * Version:     1.0
 */
public class WXPayEntryActivity extends FragmentActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, MyConfig.APP_ID, false);
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {

        LogUtils.loge("resp.errCode:" + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                RxBus.getInstance().post(new RxBean(MyConfig.PAY_STATUS_SUCCESS));
            } else if (resp.errCode == -2) {
                Toast.makeText(this, "取消支付", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "支付失败", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
