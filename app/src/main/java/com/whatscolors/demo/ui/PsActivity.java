package com.whatscolors.demo.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PsActivity extends BaseActivity {

    @Bind(R.id.ps_back)
    FrameLayout psBack;
    @Bind(R.id.edit_em)
    EditText editEm;
    @Bind(R.id.ps_to)
    TextView psTo;

    @Override
    protected void rxBusCallBack(RxBean rxBean) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ps;
    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.ps_to, R.id.ps_back})
    public void ChlickTo(View view) {
        switch (view.getId()) {
            case R.id.ps_to:
                String emstr = editEm.getText().toString();
                if (TextUtils.isEmpty(emstr)) {
                    ToastUitl.showShort(getString(R.string.tip_1));
                    return;
                } else if (!Utils.isEmail(emstr)) {
                    ToastUitl.showShort(getString(R.string.tip_2));
                    return;
                }
                toGetPs(emstr);

                break;

            case R.id.ps_back:
                finish();
                break;


        }
    }

    private void toGetPs(String emstr) {

        Api.getDefault().getPsBean(AESUtil.encode(AESUtil.toGsonRegister()), emstr).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get("code").getAsInt() == 200) {

                            ToastUitl.showShort(R.string.tip_3);
                            finish();
                        } else if (jsonObject.get("message") != null) {
                            ToastUitl.showShort(jsonObject.get("message").getAsString());
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUitl.showShort(R.string.tip_4);

                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );

    }


}
