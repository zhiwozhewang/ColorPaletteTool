package com.whatscolors.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.RxSubscriber;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.whatscolors.demo.utils.Utils.getFbStr;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class LoginActivity extends BaseActivity {
    @Bind(R.id.edit_em)
    EditText editEm;
    @Bind(R.id.edit_ps)
    EditText editPs;
    @Bind(R.id.edit_ps_im)
    CheckBox editPsIm;
    @Bind(R.id.lg_to)
    TextView lgTo;
    @Bind(R.id.lg_lost_ps)
    TextView lgLostPs;
    @Bind(R.id.lg_to_rg)
    TextView lgToRg;
    @Bind(R.id.ic_1)
    LinearLayout ic1;
    @Bind(R.id.lg_help)
    TextView lg_help;

    private boolean isuserotp = false;

    @Override
    protected void initRxBus() {
        //重写该方法  不初始化eventbus
    }

    @Override
    protected void rxBusCallBack(RxBean rxBean) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        editPsIm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editPs.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editPs.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });
//        editPsIm.setChecked(false);

    }

    @OnClick({R.id.lg_to, R.id.lg_lost_ps, R.id.lg_to_rg, R.id.lg_help})
    public void ChlickTo(View view) {
        switch (view.getId()) {
            case R.id.lg_to:
                if (isReady()) {
                    toLogin();
                }
                break;
            case R.id.lg_lost_ps:
                startActivity(PsActivity.class);
                break;
            case R.id.lg_to_rg:
                startActivity(RegistActivity.class);
                break;
            case R.id.lg_help:
                Intent intent2 = new Intent(mContext, WebActivity.class);
                intent2.putExtra("name", getString(R.string.str_fb));
                intent2.putExtra("url", getFbStr());
                startActivity(intent2);
                break;

        }
    }

    private void toLogin() {
        String oldemail = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "email");
        if (!editEm.getText().toString().equalsIgnoreCase(oldemail)) {
            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "user_key", null);
        }
        getObservable().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new RxSubscriber<JsonObject>(mContext) {

                    @Override
                    protected void _onNext(JsonObject jsonObject) {
                        int code = jsonObject.get("code").getAsInt();
//                        isuserotp = false;
                        if (code == 200) {
                            JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();
                            String string = jsonObject1.get("user_key").getAsString();
                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "user_key", string);
                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "email", editEm.getText().toString());
                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "password", editPs.getText().toString());

                            SPUtils.setIsLogin(BaseApplication.getAppContext(), true);
                            startActivity(FirstActivity.class);
                            finish();
                        } else if (code == MyConfig.USER_KEY_LOGIN_LIMIT) {
                            ToastUitl.showLong(getResources().getString(R.string.str_login_abnormal));
                        } else if (code == MyConfig.USER_KEY_PASSWORD_ONCE_ONLY) {
//                            isuserotp = true;
                            showOtpDialog();
                        } else {
                            if (isuserotp) {
                                isuserotp = false;
                            }
                            ToastUitl.showLong(jsonObject.get("message").getAsString());
                        }
                    }

                    @Override
                    protected void _onError(int code, String message) {
                        ToastUitl.showShort(message);
                    }
                }
        );
//        new Observer<JsonObject>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(JsonObject jsonObject) {
//
//                if (jsonObject.get("code").getAsInt() == 200) {
//                    JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();
//                    String string = jsonObject1.get("user_key").getAsString();
//                    SPUtils.setSharedStringData(mContext, "user_key", string);
//                    SPUtils.setIsLogin(mContext, true);
//                    startActivity(FirstActivity.class);
//                    finish();
//                } else {
//                    ToastUitl.showShort(jsonObject.get("message").getAsString());
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        }

    }

    private Observable getObservable() {
        if (isuserotp) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("otp_code", editPs.getText().toString());
            return Api.getDefault().getLoginBeanOtp(AESUtil.encode(AESUtil.toGson()), jsonObject);
        } else
            return Api.getDefault().getLoginBean(AESUtil.encode(AESUtil.toGsonRegister()), initMap());
    }

    private Map initMap() {
        Map map = new HashMap();
        map.put("email", editEm.getText().toString());
        map.put("password", editPs.getText().toString());

        return map;
    }


    public boolean isReady() {

        boolean isready = false;
        String emstr = editEm.getText().toString();

        if (TextUtils.isEmpty(emstr)) {
            ToastUitl.showShort(R.string.tip_5);
            return isready;
        }
        if (!Utils.isEmail(emstr)) {
            ToastUitl.showShort(R.string.tip_6);
            return isready;
        }
        String psstr = editPs.getText().toString();

        if (TextUtils.isEmpty(psstr)) {
            ToastUitl.showShort(R.string.tip_7);
            return isready;
        }
//        if (!Utils.isPassword(psstr)) {
//            ToastUitl.showShort("Please enter a 6-16 digit combination that contains only Numbers or letters");
//            return isready;
//        }

        return true;

    }

    public void showOtpDialog() {
        String TAG = "PsChange";
        FragmentManager fragmentManager = getSupportFragmentManager();
        OtpDialog bottomDialogFragment = (OtpDialog) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = new OtpDialog();
            new DialogFragment();
        }

        if (null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            bottomDialogFragment.show(fragmentManager, TAG);
        }

    }



}
