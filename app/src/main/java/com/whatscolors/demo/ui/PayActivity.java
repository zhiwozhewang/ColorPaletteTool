package com.whatscolors.demo.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.LogUtils;
import com.whatscolors.demo.utils.PayUtils;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.whatscolors.demo.MyConfig.ids_google;
import static com.whatscolors.demo.MyConfig.ids_wechat;

import androidx.annotation.Nullable;

public class PayActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, PurchasesUpdatedListener {
    @Bind(R.id.pay_back)
    FrameLayout myBack;
    @Bind(R.id.pay_rb1)
    RadioButton payRb1;
    @Bind(R.id.pay_rb2)
    RadioButton payRb2;
    @Bind(R.id.pay_rb3)
    RadioButton payRb3;
    @Bind(R.id.pay_upgrade)
    TextView payUpgrade;
    private int[] ints = {0, 1, 2};
    private int anInt = ints[1];
    private BillingClient mBillingClient;
    private String string1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    isReportFailed();
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView() {
        payRb1.setOnCheckedChangeListener(this);
        payRb2.setOnCheckedChangeListener(this);
        payRb3.setOnCheckedChangeListener(this);
        string1 = getString(R.string.str_pay);
        if ("google".equals(string1)) {
            PayUtils.init(this, new PayUtils.CheckPayLister() {
                @Override
                public void onError(String debugMessage) {
                    ToastUitl.showShort(debugMessage);
                }

                @Override
                public void onSucess() {
                    PayUtils.queryPurchase();
                }

            });
            isReportFailed();
        }


    }

    private void isReportFailed() {
        String user_key = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key");
        if (!TextUtils.isEmpty(user_key)) {
            String json = SPUtils.getSharedStringData(BaseApplication.getAppContext(), user_key);
            if (!TextUtils.isEmpty(json)) {
                PayUtils.reportToServer(json);
            }
        }
    }

    String string = "{\"orderId\":\"GPA.3362-8817-6229-89765\",\"packageName\":\"com.whatscolors.demo.takephoto\",\"productId\":\"month_1\",\"purchaseTime\":1574853373191,\"purchaseState\":0,\"purchaseToken\":\"hmfnkecgaacgcghogblmoljn.AO-J1OxWePY_eDDTReWM6FUJ6Qf_B6ZEq7nRIH7_2agQj6iPrjHlMdvHhZknv_sMbdtjjUgImDjxpHQoCBxW7NprmuyG1ps5wqtj5Xv818NXYerrdbpH5Ix_I8BJt9Yon2InkrUzqNKG\",\"autoRenewing\":true}";

    @OnClick({R.id.pay_back, R.id.pay_upgrade})
    public void ClickTo(final View view) {
        switch (view.getId()) {
            case R.id.pay_back:
                finish();
                break;
            case R.id.pay_upgrade:
                if ("google".equals(string1)) {
//                    PayUtils.reportToServer("{\"orderId\":\"GPA.3360-2836-3377-90676\",\"packageName\":\"com.whatscolors.demo.takephoto\",\"productId\":\"quarter_v2\",\"purchaseTime\":1576743352077,\"purchaseState\":0,\"purchaseToken\":\"ejbnpenoaiaiipejlpbpapka.AO-J1Oxrn4rqSxJR-33PWTTtGOKc3Vum2xLJrGLbasO6Gyq05ZUfpi7SWLWObH1Qyz9H3_fMJf_o5X64BuuF3w-ZE4gyom_HHL04e4POEjRzMKs2gyYSP_vXLGfo2NfHYcgX-Amj3W6Z\",\"acknowledged\":false}");
                    PayUtils.queryPurchases((Activity) mContext, ids_google[anInt], new PayUtils.CheckPayLister() {
                        @Override
                        public void onError(final String debugMessage) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUitl.showShort(debugMessage);
                                }
                            });
                        }

                        @Override
                        public void onSucess() {

                        }
                    });
                } else
                    PayUtils.unifiedOrder(ids_wechat[anInt], mContext);
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            switch (compoundButton.getId()) {
                case R.id.pay_rb1:
                    anInt = ints[1];
                    payRb2.setChecked(false);
                    payRb3.setChecked(false);
                    break;
                case R.id.pay_rb2:
                    anInt = ints[2];
                    payRb1.setChecked(false);
                    payRb3.setChecked(false);
                    break;
                case R.id.pay_rb3:
                    anInt = ints[0];
                    payRb2.setChecked(false);
                    payRb1.setChecked(false);
                    break;
            }
        }

    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        LogUtils.loge("onPurchasesUpdated code = " + billingResult.getResponseCode() + " ,  msg = " + billingResult.getDebugMessage());
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                PayUtils.handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            ToastUitl.showShort(R.string.tip_44);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            ToastUitl.showShort(R.string.tip_45);
        } else {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayUtils.destroy();

        if (handler != null) {
            handler.removeMessages(1000);
        }
    }


    @Override
    protected void rxBusCallBack(RxBean rxBean) {
        if (rxBean.payStatus == MyConfig.PAY_STATUS_SUCCESS) {
            SPUtils.setSharedIntData(BaseApplication.getAppContext(), "status", 1);
            PayUtils.checkPay();
            ToastUitl.showShort(R.string.pay_success);
            finish();
        } else if (rxBean.payStatus == MyConfig.SEND_STATUS_FAILED) {
            handler.sendEmptyMessageDelayed(1000, 60000);
        }
    }


}
