package com.whatscolors.demo.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonObject;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.RxSubscriber;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.ui.LoginAgainDialog;
import com.whatscolors.demo.ui.OutUserKeyDialog;
import com.whatscolors.demo.ui.PasswordChangedDialog;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.RequestPermissionCallBack;
import com.whatscolors.demo.utils.RxBus;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 基类
 */
public abstract class BaseActivity extends FragmentActivity {//implements CustomAdapt
    public Context mContext;
    private boolean islogin_state = false;
    private UserStateLister userStateLister;
    private final int mRequestCode = 1024;
    private RequestPermissionCallBack mRequestPermissionCallBack;
    public boolean isOnpause = false;
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mContext = this;
        //获得当前activity的最外层view
//        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);

        initView();
        initRxBus();
        islogin_state = SPUtils.getIsLogin(mContext);
    }

    /**
     * 2.设置 APP界面屏幕亮度值方法
     **/
    public void setAppScreenBrightness(int birghtessValue) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = birghtessValue / 255.0f;
        window.setAttributes(lp);
    }

    protected void initRxBus() {
        if (compositeDisposable != null) {
            return;
        }
        compositeDisposable = new CompositeDisposable();

        RxBus.getInstance().tObservable(RxBean.class)
                .subscribe(new Observer<RxBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(RxBean rxBean) {

                        if (rxBean.payStatus == MyConfig.USER_KEY_OUTTIME || rxBean.payStatus == MyConfig.USER_KEY_WRONG) {
                            showOutOfDateUserKeyDialog();
//                            toRefreshUserKey();
                        } else if (rxBean.payStatus == MyConfig.USER_KEY_LOGIN_ABNORMAL && !isOnpause) {
                            showLoginAgainDialog();
                        } else if (rxBean.payStatus == MyConfig.USER_KEY_PASSWORD_CHANGED && !isOnpause) {
                            showPsChangedDialog();
                        } else {
                            rxBusCallBack(rxBean);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    protected abstract void rxBusCallBack(RxBean rxBean);


    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
        //设置昼夜主题
//        initTheme();
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
//        SetStatusBarColor();
        //沉浸
//        SetTranslanteBar();
//        hideBottomUIMenu();
        setBarTransparent();
    }

    /*********************
     * 子类实现
     *****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //初始化view
    public abstract void initView();

    public void setUserStateLister(UserStateLister userStateLister) {
        this.userStateLister = userStateLister;
    }

    /**
     * 隐藏导航栏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                    //                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    //设置状态栏和导航栏透明的方式
    protected void setBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//设置透明导航栏
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);//将状态栏设置成白色
            getWindow().setNavigationBarColor(Color.TRANSPARENT);//将导航栏设置为透明色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//谷歌原生方式改变状态栏文字颜色成深色
        }

    }

    //    谷歌原生方式改变状态栏文字颜色，非常简单。
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
//    小米系统下状态栏文字颜色的修改
//    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
//        boolean result = false;
//        Window window = activity.getWindow();
//        if (window != null) {
//            Class clazz = window.getClass();
//            try {
//                int darkModeFlag = 0;
//                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
//                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
//                darkModeFlag = field.getInt(layoutParams);
//                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
//                if (dark) {
//                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
//                } else {
//                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
//                }
//                result = true;
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RomUtils.isMiUIV7OrAbove()) {
//                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
//                    if (dark) {
//                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                    } else {
//                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//                    }
//                }
//            } catch (Exception e) {
//
//            }
//        }
//        return result;
//    }
//    魅族系统状态栏文字颜色修改
//    private static boolean setFlymeLightStatusBar(Activity activity, boolean dark) {
//        boolean result = false;
//        if (activity != null) {
//            try {
//                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//                Field darkFlag = WindowManager.LayoutParams.class
//                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
//                Field meizuFlags = WindowManager.LayoutParams.class
//                        .getDeclaredField("meizuFlags");
//                darkFlag.setAccessible(true);
//                meizuFlags.setAccessible(true);
//                int bit = darkFlag.getInt(null);
//                int value = meizuFlags.getInt(lp);
//                if (dark) {
//                    value |= bit;
//                } else {
//                    value &= ~bit;
//                }
//                meizuFlags.setInt(lp, value);
//                activity.getWindow().setAttributes(lp);
//                result = true;
//            } catch (Exception e) {
//            }
//        }
//        return result;
//    }

//    一旦用谷歌原生设置状态栏文字颜色的方法进行设置的话，因为一直会携带SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN这个flag，那么默认界面会变成全屏模式，需要在根布局中设置FitsSystemWindows属性为true，所以我在基类的 process方法中加入如下的代码。
//    @Override
//    protected void process(Bundle savedInstanceState) {
//        // 华为,OPPO机型在StatusBarUtil.setLightStatusBar后布局被顶到状态栏上去了
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            View content = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
//            if (content != null) {// && !isUseFullScreenMode()
//                content.setFitsSystemWindows(true);
//            }
//        }
//    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    /**
     * 短暂显示Toast提示(来自String)
     **/
    public void showShortToast(String text) {
        ToastUitl.showShort(text);
    }

    /**
     * 短暂显示Toast提示(id)
     **/
    public void showShortToast(int resId) {
        ToastUitl.showShort(resId);
    }

    /**
     * 长时间显示Toast提示(来自res)
     **/
    public void showLongToast(int resId) {
        ToastUitl.showLong(resId);
    }

    /**
     * 长时间显示Toast提示(来自String)
     **/
    public void showLongToast(String text) {
        ToastUitl.showLong(text);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isOnpause = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (islogin_state != SPUtils.getIsLogin(this) && userStateLister != null) {
            userStateLister.initState();
            islogin_state = SPUtils.getIsLogin(this);
        }
        //友盟统计
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
//        MobclickAgent.onPause(this);
        isOnpause = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (null != compositeDisposable && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }


    public interface UserStateLister {
        void initState();
    }


    /**
     * 权限请求结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        StringBuilder permissionName = new StringBuilder();
        for (String s : permissions) {
            permissionName = permissionName.append(s + "\r\n");
        }
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        hasAllGranted = false;
                        //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new AlertDialog.Builder(mContext).setTitle(getResources().getString(R.string.tip_54))//设置对话框标题
                                    .setMessage(permissionName +
                                            getResources().getString(R.string.tip_55))//设置显示的内容
                                    .setPositiveButton(getResources().getString(R.string.dialog_tip3), new DialogInterface.OnClickListener() {//添加确定按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                            //TODO Auto-generated method stub
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.tip_56), new DialogInterface.OnClickListener() {//添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//响应事件
                                    dialog.dismiss();
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mRequestPermissionCallBack.denied();
                                }
                            }).show();//在按键响应事件中显示此对话框
                        } else {
                            //用户拒绝权限请求，但未选中“不再提示”选项
                            mRequestPermissionCallBack.denied();
                        }
                        break;
                    }
                }
                if (hasAllGranted) {
                    mRequestPermissionCallBack.granted();
                }
            }
        }
    }

    /**
     * 发起权限请求
     *
     * @param context
     * @param permissions
     * @param callback
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   RequestPermissionCallBack callback) {
        this.mRequestPermissionCallBack = callback;
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    new AlertDialog.Builder(mContext).setTitle(getResources().getString(R.string.tip_54))//设置对话框标题
                            .setMessage(permissionNames + getResources().getString(R.string.tip_55))//设置显示的内容
                            .setPositiveButton(getResources().getString(R.string.dialog_tip3), new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    //TODO Auto-generated method stub
                                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                                }
                            }).show();//在按键响应事件中显示此对话框
                } else {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                }
                break;
            }
        }
        if (isAllGranted) {
            mRequestPermissionCallBack.granted();
            return;
        }
    }


    public void toRefreshUserKey() {
        String email = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "email");
        String pas = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "password");

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pas)) {
            return;
        }
        Map map = new HashMap();
        map.put("email", email);
        map.put("password", pas);

        Api.getDefault().getLoginBean(AESUtil.encode(AESUtil.toGsonRegister()), map).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new RxSubscriber<JsonObject>(mContext) {

                    @Override
                    protected void _onNext(JsonObject jsonObject) {
                        if (jsonObject.get("code").getAsInt() == 200) {
                            JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();
                            String string = jsonObject1.get("user_key").getAsString();
                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "user_key", string);
                        }
                    }

                    @Override
                    protected void _onError(int code, String message) {
//                        ToastUitl.showShort(message);
                    }
                }
        );
    }

    public void showOutOfDateUserKeyDialog() {
        String TAG = "userkey";
        FragmentManager fragmentManager = getSupportFragmentManager();
        OutUserKeyDialog bottomDialogFragment = (OutUserKeyDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new OutUserKeyDialog();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }

    public void showLoginAgainDialog() {
        String TAG = "LoginAgain";
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginAgainDialog bottomDialogFragment = (LoginAgainDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new LoginAgainDialog();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }

    public void showPsChangedDialog() {
        String TAG = "PsChange";
        FragmentManager fragmentManager = getSupportFragmentManager();
        PasswordChangedDialog bottomDialogFragment = (PasswordChangedDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new PasswordChangedDialog();
            new DialogFragment();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
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
