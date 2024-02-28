package com.whatscolors.demo.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.base.BaseFragmentDialog;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.Utils;

import butterknife.Bind;

/**
 * Author:      wangwei
 * Date:        2020/3/17 13:36
 * Version:     1.0
 */
public class UpdateDialog extends BaseFragmentDialog implements View.OnClickListener {
    @Bind(R.id.text_dl_content)
    TextView textDlContent;
    @Bind(R.id.text_dl_no)
    TextView textDlNo;
    @Bind(R.id.text_dl_yes)
    TextView textDlYes;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_dialog_update;
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
            dismiss();
            if (!Utils.toMarket(BaseApplication.getAppContext())) {
                ToastUitl.showLong(getString(R.string.tip_53));
            }
        } else if (v.getId() == R.id.text_dl_content) {
//            Intent intent = new Intent(getActivity(), WebActivity.class);
//            intent.putExtra("name", getString(R.string.privacy));
//            intent.putExtra("url", "file:///android_asset/privacy_policy.html");
//            startActivity(intent);
        }
    }
}
