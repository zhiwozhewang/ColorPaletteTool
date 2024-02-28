package com.whatscolors.demo.takephoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;

import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.GifBean;
import com.whatscolors.demo.api.VersionBean;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.service.UpdateGifService;
import com.whatscolors.demo.ui.FirstActivity;
import com.whatscolors.demo.ui.LoginActivity;
import com.whatscolors.demo.ui.PrivacyDialog;
import com.whatscolors.demo.ui.UpdateDialog;
import com.whatscolors.demo.ui.UpdateToNewDialog;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.LogUtils;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.Utils;
import com.whatscolors.demo.utils.http.RespDTO;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.whatscolors.demo.MyConfig.GIF_VERSION;
import static com.whatscolors.demo.utils.DownLoadUtils.getGifPath;
import static com.whatscolors.demo.utils.DownLoadUtils.isGifFileHas;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


public class SplashActivity extends FragmentActivity {//implements CustomAdapt

    @Bind(R.id.gif_im)
    GifImageView gifIm;
    private MyHandler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//设置透明导航栏
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);//将状态栏设置成白色
            getWindow().setNavigationBarColor(Color.TRANSPARENT);//将导航栏设置为透明色
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        handler = new MyHandler(this);
        handler.sendEmptyMessageDelayed(1000, 2000);

//        Uri data = getIntent().getData();
//        Log.i(TAG, "host = " + data.getHost() + " path = " + data.getPath() + " query = " + data.getQuery());
//        String param = data.getQueryParameter("goodsId");

        if (isGifFileHas() && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//文件路径
            try {
                GifDrawable gifFromPath = new GifDrawable(getGifPath());
                LogUtils.loge("GifDrawable:" + gifFromPath.toString());
                gifIm.setBackground(gifFromPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            gifIm.setBackgroundResource(R.drawable.loading_logo);
        }

    }

    public void showDialog() {
        String TAG = "privacy";
        FragmentManager fragmentManager = getSupportFragmentManager();
        PrivacyDialog bottomDialogFragment = (PrivacyDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new PrivacyDialog();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }

    private void getVersionUpdate() {

        Api.getDefault().getVersionUpdate(AESUtil.encode(AESUtil.toGson())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<RespDTO<VersionBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RespDTO<VersionBean> versionBeanRespDTO) {
                        if (versionBeanRespDTO.data != null) {
                            LogUtils.loge("版本更新返回:" + versionBeanRespDTO.data.toString());
                        } else {
                            LogUtils.loge("版本更新返回:" + versionBeanRespDTO.toString());


                        }

                        if (versionBeanRespDTO.code == 200) {
                            VersionBean jsonObject1 = versionBeanRespDTO.data;//jsonObject.get("data").getAsJsonObject();
                            try {
                                LogUtils.loge("版本更新返回:" + versionBeanRespDTO.data.toString());
                                String newversion = jsonObject1.version.replace(".", "");
                                if (Integer.valueOf(newversion) > Utils.getVersionCode(BaseApplication.getAppContext())) {
//                                    showUpdateDialog();
                                    showToUpdateDialog(jsonObject1.version, jsonObject1.download_url);
                                } else
                                    toMain();
                            } catch (Exception e) {
                                e.printStackTrace();
                                toMain();
                            }

                        } else {
                            toMain();
//                            ToastUitl.showShort(jsonObject.get("message").getAsString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        toMain();
                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );


    }

    private void toMain() {


        if (SPUtils.getSharedBooleanData(BaseApplication.getAppContext(), "isagree")) {
            if (SPUtils.getIsLogin(BaseApplication.getAppContext()) && !TextUtils.isEmpty(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "password")))
                startActivity(new Intent(SplashActivity.this, FirstActivity.class));
            else
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));

            finish();
        } else {
            showDialog();
        }

    }

    public void showUpdateDialog() {
        String TAG = "UpdateDialog";
        FragmentManager fragmentManager = getSupportFragmentManager();
        UpdateDialog bottomDialogFragment = (UpdateDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new UpdateDialog();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }

    public void showToUpdateDialog(String version, String download_url) {
        String TAG = "ToUpdateDialog";
        FragmentManager fragmentManager = getSupportFragmentManager();
        UpdateToNewDialog bottomDialogFragment = (UpdateToNewDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new UpdateToNewDialog();
            Bundle bundle = new Bundle();
            bundle.putString("apkurl", download_url);
            bundle.putString("version", version);
            bottomDialogFragment.setArguments(bundle);
        }
        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }

    private void getUpdateGif() {

        Api.getDefault().getUpdateGif(AESUtil.encode(AESUtil.toGson())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<RespDTO<GifBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RespDTO<GifBean> jsonObject) {
                        LogUtils.loge("gif更新返回：" + jsonObject.toString());
                        if (jsonObject.code == 200) {
                            if (jsonObject.data == null) {
                                return;
                            }
//                            LogUtils.loge("GifBean:" + jsonObject.data.toString());

                            GIF_VERSION = SPUtils.getSharedIntData(BaseApplication.getAppContext(), "gifversion");
                            if (jsonObject.data.version > GIF_VERSION && !TextUtils.isEmpty(jsonObject.data.image_url)) {
                                Intent intent = new Intent(SplashActivity.this, UpdateGifService.class);
                                intent.putExtra("gifversion", jsonObject.data.version);
                                intent.putExtra("gifurl", jsonObject.data.image_url);
                                startService(intent);
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getVersionUpdate();
                    }

                    @Override
                    public void onComplete() {
                        getVersionUpdate();
                    }
                }
        );


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<SplashActivity> reference;

        public MyHandler(SplashActivity splashActivity) {
            reference = new WeakReference<>(splashActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    reference.get().getUpdateGif();
                    break;
            }
        }
    }

//    @Override
//    public boolean isBaseOnWidth() {
//        return true;
//    }
//
//    @Override
//    public float getSizeInDp() {
//        return 125;
//    }
}
