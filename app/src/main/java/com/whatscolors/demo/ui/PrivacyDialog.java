package com.whatscolors.demo.ui;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.base.BaseFragmentDialog;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.SPUtils;

import butterknife.Bind;

import static com.whatscolors.demo.utils.Utils.isZh;

import androidx.fragment.app.DialogFragment;

/**
 * Author:      wangwei
 * Date:        2020/3/17 13:36
 * Version:     1.0
 */
public class PrivacyDialog extends BaseFragmentDialog implements View.OnClickListener {
    @Bind(R.id.text_dl_content)
    TextView textDlContent;
    @Bind(R.id.text_dl_no)
    TextView textDlNo;
    @Bind(R.id.text_dl_yes)
    TextView textDlYes;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_dialog;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        textDlNo.setOnClickListener(this);
        textDlYes.setOnClickListener(this);
        textDlContent.setOnClickListener(this);
        String str;
        if (isZh(getActivity())) {
            str = "请你务必审慎阅读、充分理解“服务协议”和“隐私政策”各条款，包括但不限于：为了向你提供即时通讯、内容分享等服务，我们需要收集你的设备信息、操作日志等个人信息。\n你可阅读<font color='#3F51B5'>服务协议</font>和<font color='#3F51B5'>隐私政策</font>了解详细信息。如果你同意，请点击“同意”开始接受我们得服务。";

        } else
            str = "Please carefully read and fully understand the terms of the Service Agreement and the Privacy Policy, including but not limited to: in order to provide you with instant messaging, content sharing and other services, we need to collect your device information, operation log and other personal information.you can read<font color='#3F51B5'> Service agreements </font> and <font color='#3F51B5'> privacy policy </font> for details. If you agree, please click \"Agree \" to start accepting our services";


        textDlContent.setText(Html.fromHtml(str));//getResources().getString(R.string.privcy_tips))
    }

    @Override
    protected void initStyle() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomDialog);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_dl_no) {
            dismiss();
            getActivity().finish();
            System.exit(0);
        } else if (v.getId() == R.id.text_dl_yes) {

            SPUtils.setSharedBooleanData(getActivity(), "isagree", true);
            if (SPUtils.getIsLogin(BaseApplication.getAppContext()))
                startActivity(new Intent(getActivity(), FirstActivity.class));
            else
                startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();

        } else if (v.getId() == R.id.text_dl_content) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("name", getString(R.string.privacy));
            intent.putExtra("url", getString(R.string.str_privacy_policy));
            startActivity(intent);
        }
    }
}
