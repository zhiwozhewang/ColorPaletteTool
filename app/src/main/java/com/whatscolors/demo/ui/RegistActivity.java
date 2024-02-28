package com.whatscolors.demo.ui;

import android.content.Intent;
import android.text.Html;
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
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegistActivity extends BaseActivity {
    @Bind(R.id.edit_fn)
    EditText editFn;
    @Bind(R.id.edit_ln)
    EditText editLn;
    @Bind(R.id.edit_em)
    EditText editEm;
    @Bind(R.id.edit_ps_rg)
    EditText editPsRg;
    @Bind(R.id.edit_ps_rg_im)
    CheckBox editPsRgIm;
    @Bind(R.id.edit_cps_rg)
    EditText editCpsRg;
    @Bind(R.id.edit_cps_rg_im)
    CheckBox editCpsRgIm;
    @Bind(R.id.rg_to)
    TextView rgTo;
    @Bind(R.id.rg_conditions)
    TextView rgConditions;
    @Bind(R.id.rg_to_login)
    TextView rgToLogin;
    @Bind(R.id.ic_1)
    LinearLayout ic1;
    boolean ispshide = true, iscpshide = true;

    @Override
    protected void rxBusCallBack(RxBean rxBean) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void initView() {
        editPsRgIm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editPsRgIm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editPsRgIm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        editCpsRgIm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editCpsRgIm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editCpsRgIm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
//        rgConditions.setText(getString(R.string.str_terms));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            result = Html.fromHtml(mNews.getTitle(),Html.FROM_HTML_MODE_LEGACY);
            rgConditions.setText(Html.fromHtml(getResources().getString(R.string.str_terms), Html.FROM_HTML_MODE_LEGACY).toString());

        } else {
            rgConditions.setText(Html.fromHtml(getResources().getString(R.string.str_terms)));

        }
    }

    @OnClick({R.id.rg_conditions, R.id.rg_to_login, R.id.rg_to, R.id.edit_cps_rg_im, R.id.edit_ps_rg_im})
    public void ChlickTo(View view) {
        switch (view.getId()) {
            case R.id.rg_conditions:
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("name", R.string.tip_8);
                intent.putExtra("url", getString(R.string.Str_Terms_Conditions));
                startActivity(intent);
                break;
            case R.id.rg_to_login:
//                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.rg_to:
                if (isReady()) {
                    toRg();
                }
                break;
            case R.id.edit_cps_rg_im:
                if (ispshide) {
                    ispshide = false;
                    editPsRg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editPsRg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ispshide = true;
                }
                break;
            case R.id.edit_ps_rg_im:
                if (iscpshide) {
                    iscpshide = false;
                    editCpsRg.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editCpsRg.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iscpshide = true;
                }
                break;

        }
    }

    private boolean isReady() {

        String fn = editFn.getText().toString();
        String ln = editLn.getText().toString();
        String email = editEm.getText().toString();
        String ps = editPsRg.getText().toString();
        String psa = editCpsRg.getText().toString();

        if (TextUtils.isEmpty(fn)) {
            ToastUitl.showShort(R.string.tip_9);
            return false;
        }
        if (TextUtils.isEmpty(ln)) {
            ToastUitl.showShort(R.string.tip_10);
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            ToastUitl.showShort(R.string.tip_11);
            return false;
        }
        if (TextUtils.isEmpty(ps)) {
            ToastUitl.showShort(R.string.tip_12);
            return false;
        }
        if (TextUtils.isEmpty(psa)) {
            ToastUitl.showShort(R.string.tip_13);
            return false;
        }
        if (!Utils.isEmail(email)) {
            ToastUitl.showShort(R.string.tip_14);
            return false;
        }
        if (!Utils.isPassword(ps)) {
            ToastUitl.showShort(R.string.tip_15);
            return false;
        }
        if (!Utils.isPassword(psa)) {
            ToastUitl.showShort(R.string.tip_15);
            return false;
        }
        if (!ps.equalsIgnoreCase(psa)) {
            ToastUitl.showShort(R.string.tip_17);
            return false;
        }

        return true;
    }

    private void toRg() {

        Api.getDefault().getRegistBean(AESUtil.encode(AESUtil.toGsonRegister()), initMap()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get("code").getAsInt() == 200) {
                            finish();
                        } else {
                            ToastUitl.showShort(jsonObject.get("message").getAsString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                }
        );

    }

    private Map initMap() {
        Map map = new HashMap();
        map.put("email", editEm.getText().toString());
        map.put("password", editPsRg.getText().toString());
        map.put("first_name", editFn.getText().toString());
        map.put("last_name", editLn.getText().toString());

        return map;
    }
}
