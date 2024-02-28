package com.whatscolors.demo.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.Html5WebView;
import com.whatscolors.demo.utils.SPUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class WebActivity extends BaseActivity {


    @Bind(R.id.web_title)
    TextView webTitle;
    @Bind(R.id.web_wv)
    Html5WebView webWv;
    @Bind(R.id.web_back_im)
    ImageView webBackIm;
    @Bind(R.id.web_back_text)
    TextView webBackText;
    @Bind(R.id.web_exit)
    TextView webExit;

    @Override
    protected void rxBusCallBack(RxBean rxBean) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    public void initView() {
        webTitle.setText(getIntent().getStringExtra("name"));
        webWv.loadUrl(getIntent().getStringExtra("url"));
//        if (SPUtils.getIsLogin(mContext)) {
//            webExit.setVisibility(View.VISIBLE);
//        } else
//            webExit.setVisibility(View.GONE);

    }

    @OnClick({R.id.web_back_im, R.id.web_back_text, R.id.web_exit})
    public void ChlickTo(View view) {
        switch (view.getId()) {
            case R.id.web_back_im:
            case R.id.web_back_text:
                finish();
                break;
            case R.id.web_exit:
//                SPUtils.setSharedStringData(mContext, "user_key", "");
                SPUtils.setIsLogin(mContext, false);
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

        }
    }


}
