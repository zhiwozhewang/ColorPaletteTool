package com.whatscolors.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.RxSubscriber;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.base.BaseFragmentDialog;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:      wangwei
 * Date:        2020/3/17 13:36
 * Version:     1.0
 */
public class OtpDialog extends BaseFragmentDialog implements View.OnClickListener {
    @Bind(R.id.text_dl_content)
    TextView textDlContent;
    @Bind(R.id.text_dl_no)
    TextView textDlNo;
    @Bind(R.id.text_dl_yes)
    TextView textDlYes;
    @Bind(R.id.edit_ps)
    EditText editPs;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_dialog_otp;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {

        textDlNo.setOnClickListener(this);
        textDlYes.setOnClickListener(this);
    }

    @Override
    protected void initStyle() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomDialog);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_dl_no) {
            dismiss();
//            SPUtils.setSharedStringData(getActivity(), "user_key", "");
            SPUtils.setIsLogin(getActivity(), false);
//            startActivity(PsActivity.class);
            Intent intent2 = new Intent(getActivity(), PsActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            getActivity().finish();

        } else if (v.getId() == R.id.text_dl_yes) {

            isReady();


        } else if (v.getId() == R.id.text_dl_content) {

        }
    }

    public boolean isReady() {

        boolean isready = false;
        String psstr = editPs.getText().toString();

        if (TextUtils.isEmpty(psstr)) {
            ToastUitl.showShort(R.string.tip_7);
            return isready;
        }
        toLogin();
        return true;

    }

    private void toLogin() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("otp_code", editPs.getText().toString());
        Api.getDefault()
                .getLoginBeanOtp(AESUtil.encode(AESUtil.toGsonRegister()), jsonObject)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new RxSubscriber<JsonObject>(getActivity()) {

                    @Override
                    protected void _onNext(JsonObject jsonObject) {


                        int code = jsonObject.get("code").getAsInt();
//                        isuserotp = false;
                        if (code == 200) {
                            JsonObject jsonObject1 = jsonObject.get("data").getAsJsonObject();
                            String string = jsonObject1.get("user_key").getAsString();
                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "user_key", string);
//                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "email", editEm.getText().toString());
//                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "password", editPs.getText().toString());
                            SPUtils.setIsLogin(BaseApplication.getAppContext(), true);
                            startActivity(FirstActivity.class);
//
                            dismiss();
                            getActivity().finish();

                        } else if (code == MyConfig.USER_KEY_LOGIN_LIMIT) {
                            ToastUitl.showLong(getResources().getString(R.string.str_login_abnormal));
                        } else {
                            ToastUitl.showLong(jsonObject.get("message").getAsString());
                        }
                    }

                    @Override
                    protected void _onError(int code, String message) {
                        ToastUitl.showShort(message);
                    }
                }
        );

    }

}
