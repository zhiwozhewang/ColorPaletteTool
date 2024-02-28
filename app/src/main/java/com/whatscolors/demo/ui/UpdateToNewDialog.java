package com.whatscolors.demo.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import com.whatscolors.demo.base.BaseFragmentDialog;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.view.NumberProgressBar;

import butterknife.Bind;
import util.AppDownloadManager;

/**
 * Author:      wangwei
 * Date:        2020/3/17 13:36
 * Version:     1.0
 */
public class UpdateToNewDialog extends BaseFragmentDialog implements View.OnClickListener, AppDownloadManager.OnUpdateListener {
    @Bind(R.id.title)
    TextView t_title;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.btn_update)
    Button btn_update;
    @Bind(R.id.npb_progress)
    NumberProgressBar npb_progress;
    @Bind(R.id.layout_pro)
    LinearLayout layout_pro;
    @Bind(R.id.iv_close)
    AppCompatImageView ivClose;


    public AppDownloadManager appDownloadManager;
    public boolean isflag;
    private String title, desc, version, url;

    @Override
    protected int getLayoutResource() {
        return R.layout.xupdate_dialog_app;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        setCancelable(false);
        ivClose.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        //
        url = getArguments().getString("apkurl");
        version = getArguments().getString("version");
        title = getResources().getString(R.string.app_name);
        desc = getString(R.string.str_update_version);
        t_title.setText(title + ":");
        tv_title.setText(getString(R.string.str_is_tonew, version));//"是否升级到" + updateInfo.getVersion_number() + "版本？");


    }

    @Override
    protected void initStyle() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomDialog);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close) {
            if (isflag) {
                ToastUitl.showLong(R.string.str_update_under);
            }

        } else if (v.getId() == R.id.btn_update) {
            showMustUpDialog();
        }
    }

    public void showMustUpDialog() {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (appDownloadManager == null) {
            appDownloadManager = new AppDownloadManager(getActivity());
            appDownloadManager.setUpdateListener(this);
            appDownloadManager.resume();
        }
        //升级
        isflag = true;
        layout_pro.setVisibility(View.VISIBLE);
        btn_update.setVisibility(View.GONE);
//        ToastUitl.showShort(R.string.str_isupdate);
        appDownloadManager.downloadApk(url, title, desc);


    }

    @Override
    public void update(int currentByte, int totalByte) {
        if (npb_progress != null) {
            npb_progress.setProgress((int) ((float) currentByte / (float) totalByte * 100));
        }
        if (currentByte == totalByte) {
            ToastUitl.showShort(R.string.str_isdownloaded);
            dismiss();
        }
    }
}
