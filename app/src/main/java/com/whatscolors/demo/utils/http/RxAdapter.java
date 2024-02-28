package com.whatscolors.demo.utils.http;

import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.utils.RxBus;
import com.whatscolors.demo.utils.ToastUitl;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: <Rx适配器><br>
 * Author:      mxdl<br>
 * Date:        2019/3/18<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class RxAdapter {


    /**
     * 线程调度器
     */
    public static SingleTransformer singleSchedulersTransformer() {
        return new SingleTransformer() {
            @Override
            public SingleSource apply(Single upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static SingleTransformer singleExceptionTransformer() {

        return new SingleTransformer() {
            @Override
            public SingleSource apply(Single observable) {
                return observable
                        .map(new HandleFuc())  //这里可以取出BaseResponse中的Result
                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer exceptionTransformer() {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable
                        .map(new HandleFuc());  //这里可以取出BaseResponse中的Result
//                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    public static ObservableTransformer exceptionTransformer_nomap() {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable
                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable t) {
            ResponseThrowable exception = ExceptionHandler.handleException(t);
            if (exception.code == ExceptionHandler.SYSTEM_ERROR.TIMEOUT_ERROR) {
                ToastUitl.showShort("网络不给力哦！");
            }
            return Observable.error(exception);
        }
    }

    private static class HandleFuc implements Function<Object, Object> {

        @Override
        public Object apply(Object o) {
            if (o instanceof RespDTO) {
                RespDTO respDTO = (RespDTO) o;
                if (respDTO.code == MyConfig.USER_KEY_OUTTIME || respDTO.code == MyConfig.USER_KEY_PASSWORD_CHANGED || respDTO.code == MyConfig.USER_KEY_LOGIN_ABNORMAL||respDTO.code == MyConfig.USER_KEY_WRONG) {//                旧版单点登录
//                    ToastUitls.showShortToast(RetrofitManager.mContext, respDTO.message);
                    RxBus.getInstance().post(new RxBean(respDTO.code));
                    return  null;
                }
            }
            return o;
        }
    }

}
