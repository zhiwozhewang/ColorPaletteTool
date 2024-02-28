package com.whatscolors.demo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.PayUtils;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;

import static com.whatscolors.demo.utils.PayUtils.buildDialog;
import static com.whatscolors.demo.utils.Utils.getFbStr;

public class MyActivity extends BaseActivity {

    @Bind(R.id.my_back)
    FrameLayout myBack;
    @Bind(R.id.my_name)
    TextView myName;
    @Bind(R.id.my_base)
    TextView myBase;
    @Bind(R.id.my_store_fr)
    FrameLayout myStoreFr;
    @Bind(R.id.my_agree_fr)
    FrameLayout myAgreeFr;
    @Bind(R.id.my_about_fr)
    FrameLayout myAboutFr;
    @Bind(R.id.my_upgrade)
    TextView myUpgrade;
    @Bind(R.id.my_restore)
    TextView myRestore;
    @Bind(R.id.my_version)
    TextView myVersion;
    @Bind(R.id.my_date)
    TextView myDate;
    @Bind(R.id.web_exit)
    TextView webExit;


    @Override
    public int getLayoutId() {
        return R.layout.activity_my;
    }

    @Override
    public void initView() {
        myVersion.setText(getResources().getString(R.string.str_v, Utils.getVersionName(mContext)));
        PayUtils.checkPay();
    }


    private void checkPayState() {
        int status = SPUtils.getSharedIntData(mContext, "status");
        if (status == 0 || status == 2) {
            myBase.setText(getString(R.string.tip_32));
            myUpgrade.setVisibility(View.VISIBLE);
        } else {
            myBase.setText(getString(R.string.tip_52));
            myUpgrade.setVisibility(View.GONE);
            myDate.setText(getResources().getString(R.string.epires_in, SPUtils.getSharedStringData(mContext, "valid_until")));
        }
        myName.setText(SPUtils.getSharedStringData(mContext, "first_name"));


    }

    @OnClick({R.id.web_exit, R.id.my_back, R.id.my_store_fr, R.id.my_agree_fr, R.id.my_about_fr, R.id.my_upgrade, R.id.my_restore, R.id.my_fb_fr})
    public void ChlickTo(View view) {
        switch (view.getId()) {
            case R.id.my_back:
                finish();
                break;
            case R.id.my_store_fr:
                try {
                    toStore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.my_agree_fr:
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("name", getString(R.string.privacy));
                intent.putExtra("url", getString(R.string.str_privacy_policy));
                startActivity(intent);
                break;
            case R.id.my_about_fr:
                Intent intent1 = new Intent(mContext, WebActivity.class);
                intent1.putExtra("name", getString(R.string.about_us));
//                intent1.putExtra("url", "http://hh4k.com.hk/");
                intent1.putExtra("url", getString(R.string.str_about));
                startActivity(intent1);
                break;

            case R.id.my_fb_fr:
                Intent intent2 = new Intent(mContext, WebActivity.class);
                intent2.putExtra("name", getString(R.string.str_fb));
                intent2.putExtra("url", getFbStr());
                startActivity(intent2);
                break;

            case R.id.my_upgrade:
                startActivity(PayActivity.class);
                break;
            case R.id.my_restore:
                break;
            case R.id.web_exit:

                buildDialog(mContext, getString(R.string.dialog_tip5), getString(R.string.dialog_tip4), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        SPUtils.setSharedStringData(mContext, "user_key", "");
                        SPUtils.setIsLogin(mContext, false);
                        Intent intent2 = new Intent(mContext, LoginActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                break;

        }
    }




    private void toStore() {
        //参考链接：
//http://stackoverflow.com/questions/4702204/android-market-detailsid-not-working-for-app

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
//        intent.setPackage(GoogleMarket.GOOGLE_PLAY);//这里对应的是谷歌商店，跳转别的商店改成对应的即可
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else {
            //没有应用市场，通过浏览器跳转到Google Play
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            if (intent2.resolveActivity(getPackageManager()) != null) {
                startActivity(intent2);
            } else {
                //没有Google Play 也没有浏览器
                ToastUitl.showShort(R.string.tip_33);
            }
        }

/*根据以上，同理使用以下Uri进行替换：
Uri.parse("market://search?q=pub:Author Name"); //跳转到商店搜索界面，并搜索开发者姓名
Uri.parse("market://search?q=Keyword"); //跳转到商店搜索界面，并搜索关键词
*/

    }


//    private void toGetProfile() {
//        Api.getDefault().checkPay(AESUtil.encode(AESUtil.toGson())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
//                new Observer<CheckBean>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(CheckBean checkBean) {
//                        if (checkBean.getCode() == 200) {
//                            CheckBean.DataBean jsonObject1 = checkBean.getData();
//                            if (jsonObject1 != null) {
//                                SPUtils.setSharedStringData(mContext, "first_name", jsonObject1.getFirst_name());
//                                SPUtils.setSharedStringData(mContext, "last_name", jsonObject1.getLast_name());
//                                CheckBean.DataBean.ServiceBean serviceBean = jsonObject1.getService();
//                                if (serviceBean != null) {
//                                    SPUtils.setSharedIntData(BaseApplication.getAppContext(), "status", serviceBean.getStatus());
//                                    SPUtils.setSharedBooleanData(BaseApplication.getAppContext(), "is_lifetime", serviceBean.isIs_lifetime());
//                                    SPUtils.setSharedStringData(BaseApplication.getAppContext(), "valid_until", serviceBean.isIs_lifetime() ? getResources().getString(R.string.date_forever) : serviceBean.getValid_until());
//                                }
//                                CheckBean.DataBean.UsageRecordBean usageRecord = jsonObject1.getUsageRecord();
//                                if (usageRecord != null) {
//                                    SPUtils.setSharedIntData(BaseApplication.getAppContext(), "times", usageRecord.getTimes());
//                                }
//                            }
//                        } else {
//                            ToastUitl.showShort(checkBean.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        checkPayState();
//                    }
//                }
//        );
//
//    }



    @Override
    protected void rxBusCallBack(RxBean rxBean) {
        if (rxBean.payStatus == MyConfig.USER_STATUS_SUCCESS) {
            checkPayState();
        }
    }


}
