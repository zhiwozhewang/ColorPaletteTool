package com.whatscolors.demo.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;


public class Html5WebView extends WebView {

    private ProgressBar mProgressBar;
    private Context mContext;
    private WebsiteChangeListener mWebsiteChangeListener;


    public Html5WebView(Context context) {
        super(context);
        mContext = context;

        init();
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {

        //================顶部进度条的初始化===================
        mProgressBar = new ProgressBar(mContext, null,
                android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 8);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setVisibility(GONE);
//        Drawable drawable = mContext.getResources().getDrawable(
//                R.drawable.barcolor);
//        mProgressBar.setProgressDrawable(drawable);
        // addView(mProgressBar);
        //================顶部进度条的初始化===================

        WebSettings mWebSettings = getSettings();
        // 设置是否可缩放 仅支持双击缩放，不支持触摸缩放
        mWebSettings.setSupportZoom(false);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setDisplayZoomControls(false);

        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        //设置编码格式
        mWebSettings.setDefaultTextEncodingName("utf-8");
        setWebChromeClient(new WebChromeClient());
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        }


        //支持自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);
        //mWebSettings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // setBackgroundColor(0); // 设置背景色
        //缓存数据
        saveData(mWebSettings);
        //newWin(mWebSettings);
        //  setWebChromeClient(webChromeClient);
        setWebViewClient(webViewClient);//原来的设置。
        //setWebViewClient(new NoAdWebViewClient(mContext));//去除广告，可是不起作用


    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        // mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //  if (NetStatusUtil.isConnected(mContext)) {
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        //  } else {
        //      mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        //  }

        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = mContext.getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    WebViewClient webViewClient = new WebViewClient() {
        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mWebsiteChangeListener != null) {
                mWebsiteChangeListener.onUrlChange(url);
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView view, String url) {
            addImageClickListner2();

            super.onPageFinished(view, url);
        }


    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, false, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            view.loadUrl(data);
            return true;
        }
        //=========多窗口的问题==========================================================

        //=========顶部进度条的进度更新===============================
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

//            if (newProgress == 100) {
//                mProgressBar.setVisibility(GONE);
////                isNotTop = true;
//            } else {
//                if (mProgressBar.getVisibility() == GONE)
//                    mProgressBar.setVisibility(VISIBLE);
////                if (isNotTop && mProgressBar.getVisibility() == VISIBLE) {
////                    if (mWebsiteChangeListener != null) {
////                        mWebsiteChangeListener.onWebsiteChangeBackTop();
////                        isNotTop = false;
////                    }
////                }
//             //   mProgressBar.setProgress(newProgress);
            //   }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mWebsiteChangeListener != null) {
                mWebsiteChangeListener.onWebsiteChange(title);
            }
        }
    };

    public interface WebsiteChangeListener {
        void onWebsiteChange(String title);

        void onUrlChange(String url);
//        void onWebsiteChangeBackTop();
    }

    public void setWebsiteChangeListener(WebsiteChangeListener websiteChangeListener) {
        this.mWebsiteChangeListener = websiteChangeListener;

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
//        lp.x = l;
//        lp.y = t;
//        mProgressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);

    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // 注入js函数监听
    private void addImageClickListner2() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "var imgUrl = \"\";" +
                "var filter = [\"img//EventHead.png\",\"img//kong.png\",\"hdtz//button.png\"];" +
                "var isShow = true;" +
                "for(var i=0;i<objs.length;i++){" +
                "for(var j=0;j<filter.length;j++){" +
                "if(objs[i].src.indexOf(filter[j])>=0) {" +
                "isShow = false; break;}}" +
                "if(isShow && objs[i].width>120){" +
                "imgUrl += objs[i].src + ',';isShow = true;" +
                "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imageListener.openImage(imgUrl,this.src);" +
                "    }" +
                "}" +
                "}" +
                "})()"
        );
    }

    private long last_time = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                long current_time = System.currentTimeMillis();
                long d_time = current_time - last_time;
                // LogUtil.LOG("=======================d_time", d_time);
                if (d_time < 300) {
                    last_time = current_time;
                    return true;
                } else {
                    last_time = current_time;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
