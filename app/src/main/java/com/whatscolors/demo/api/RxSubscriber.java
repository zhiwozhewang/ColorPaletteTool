package com.whatscolors.demo.api;

import android.app.Activity;
import android.content.Context;

import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.NetWorkUtils;
import com.whatscolors.demo.view.LoadingDialog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/********************使用例子********************/
/*_apiService.login(mobile, verifyCode)
        .//省略
        .subscribe(new RxSubscriber<User user>(mContext,false) {
@Override
public void _onNext(User user) {
        // 处理user
        }

@Override
public void _onError(String msg) {
        ToastUtil.showShort(mActivity, msg);
        });*/
public abstract class RxSubscriber<T> implements Observer<T> {

    public Context mContext;
    private String msg;
    private boolean showDialog;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog = true;
    }

    public void hideDialog() {
        this.showDialog = true;
    }

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, "", true);//BaseApplication.getAppContext().getString(R.string.loading)
    }

    public RxSubscriber(Context context, boolean showDialog) {
        this(context, "", showDialog);//BaseApplication.getAppContext().getString(R.string.loading)
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (showDialog && !LoadingDialog.isLoading()) {
            try {
                LoadingDialog.showDialogForLoading((Activity) mContext, msg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onComplete() {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
        e.printStackTrace();
        //网络
        if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
        }
        //服务器
//        else if (e instanceof ServerException) {
//            _onError(((ServerException) e).getCode(), e.getMessage());
//        }
        //其它
        else {
            _onError(BaseApplication.getAppContext().getString(R.string.error_net));
        }
    }

    protected abstract void _onNext(T t);

    protected void _onError(String message) {
        _onError(0, message);
    }

    protected abstract void _onError(int code, String message);

}
