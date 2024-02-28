package com.whatscolors.demo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.RxSubscriber;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.bean.CheckBean;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.bean.WechatBean;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.http.RespDTO;
import com.whatscolors.demo.utils.http.RxAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.whatscolors.demo.MyConfig.WECHAT_PACKAGE;
import static com.whatscolors.demo.MyConfig.types;

import androidx.annotation.NonNull;

/**
 * Author:      wangwei
 * Date:        2019-11-25 16:24
 * Version:     1.0
 */
public class PayUtils {
    private static BillingClient mBillingClient;
    private CheckPayLister checkPayLister;

    public static synchronized BillingClient init(PurchasesUpdatedListener listener, final CheckPayLister checkPayLister) {
        if (mBillingClient == null) {
            mBillingClient = BillingClient.newBuilder(BaseApplication.getAppContext())
                    .enablePendingPurchases() //要启用待处理的购买交易
                    .setListener(listener).build();
//            if (!mBillingClient.isReady())
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    LogUtils.loge("onBillingSetupFinished code = " + billingResult.getResponseCode() + " ,  msg = " + billingResult.getDebugMessage());
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (checkPayLister != null) {
                            checkPayLister.onSucess();
                        }
                    } else if (checkPayLister != null && !TextUtils.isEmpty(billingResult.getDebugMessage())) {
                        checkPayLister.onError(billingResult.getDebugMessage());
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.

                }
            });
        }
        return mBillingClient;
    }

    public static void queryPurchases(final Activity activity, @NonNull final String purchaseId, final CheckPayLister mListener) {

        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) {
                queryPurchases_google(activity, purchaseId, mListener, emitter);
            }

        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(


                new RxSubscriber<Void>(activity) {

                    @Override
                    protected void _onNext(Void aVoid) {

                    }

                    @Override
                    protected void _onError(int code, String message) {
                        ToastUitl.showShort(message);
                    }
                }
        );
    }

    private static void queryPurchases_google(final Activity activity, @NonNull final String purchaseId, final CheckPayLister mListener, final ObservableEmitter<Void> emitter) {

        if (mBillingClient == null) {
            return;
        }
//  v5库逻辑
      /*  List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        products.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(purchaseId)
                .setProductType(types[0])
                .build());
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();

        mBillingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                        // check billingResult
                        LogUtils.loge("onSkuDetailsResponse code = " + billingResult.getResponseCode() + " ,  msg = " + billingResult.getDebugMessage() + " , skuDetailsList = " + productDetailsList);

                    }
                }
        );*/
//        v2支付库旧有逻辑
        List<String> skuList = new ArrayList<>();
        skuList.add(purchaseId);
        skuList.add("new");  // 这个参数不能为空，值随便传
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(types[0]);//BillingClient.SkuType.INAPP
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> productDetailsList) {
                        LogUtils.loge("onSkuDetailsResponse code = " + billingResult.getResponseCode() + " ,  msg = " + billingResult.getDebugMessage() + " , skuDetailsList = " + productDetailsList);
                        if (productDetailsList == null || productDetailsList.isEmpty()) {
                            if (mListener != null) {
                                mListener.onError(activity.getString(R.string.no_goods));
                            }
                            LogUtils.loge("onSkuDetailsResponse skuDetailsList = null");
                            emitter.onComplete();

                            return;
                        }
                        SkuDetails skuDetails = null;
                        for (SkuDetails details : productDetailsList) {
                            LogUtils.loge("onSkuDetailsResponse skuDetails = " + details.toString());
                            if (purchaseId.equals(details.getSku())) {
                                skuDetails = details;
                            }
                        }
                        if (skuDetails != null) {
                            pay(activity, skuDetails);
                        } else {
                            LogUtils.loge("onSkuDetailsResponse 无对应商品");

                        }
                        emitter.onComplete();

                    }
                });
    }

    public static void pay(Activity activity, SkuDetails skuDetails) {
//v5库升级指南  https://developer.android.com/google/play/billing/integrate?hl=zh-cn#java
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        int code = mBillingClient.launchBillingFlow(activity, flowParams).getResponseCode();
        LogUtils.loge("pay code = " + code);


    }

    public static void handlePurchase(Purchase purchase) {

        final String token = purchase.getOriginalJson();

//        if (purchase.isAcknowledged()) {
//            return;
//        }
//        if (MyConfig.ids_google[0].equalsIgnoreCase(purchase.getSku())) {
//            //订阅和一次性支付都属于非消耗
//            AcknowledgePurchaseParams acknowledgePurchaseParams =
//                    AcknowledgePurchaseParams.newBuilder()
//                            .setPurchaseToken(purchase.getPurchaseToken())
//                            .setDeveloperPayload(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key"))
//                            .build();
//            mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
//                @Override
//                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
//                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
//                        reportToServer(token);
//                }
//            });
//        } else
        {

            // 对于消耗型商品 只有消费成功之后，才能真正到账，否则3天之后，会执行退款处理 测试阶段只有5分钟
            mBillingClient.consumeAsync(ConsumeParams.newBuilder()
//                            .setDeveloperPayload(SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key"))
                            .setPurchaseToken(purchase.getPurchaseToken()).build(),
                    new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                            LogUtils.loge("onConsumeResponse code = " + billingResult.getResponseCode() + " ,  msg = " + billingResult.getDebugMessage() + " , purchaseToken = " + purchaseToken);              // 消费成功  处理自己的流程，我选择先存入数据库
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                        PurchaseHistoryBean purchaseHistory = new PurchaseHistoryBean();
//                                        purchaseHistory.uid = NumberParserUtil.parseLong(UserInfoManager.create().getUserId(), -1);
//                                        purchaseHistory.orderId = purchase.getOrderId();
//                                        purchaseHistory.purchaseToken = purchase.getPurchaseToken();
//                                        purchaseHistory.developerPayload = purchase.getDeveloperPayload();
//                                        purchaseHistory.productId = purchase.getSku();
//                                        purchaseHistory.purchaseTime = purchase.getPurchaseTime();
//                                        savePurchaseHistory(purchaseHistory, needToast);
                                reportToServer(token);
                            } else {
                                // 消费失败,后面查询消费记录后再次消费，否则，就只能等待退款
                            }
                        }
                    });
        }
//        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)

    }

    public static void reportToServer(final String json) {

        LogUtils.loge("上报的json:" + json);
        JsonObject purchaseToken = new Gson().fromJson(json, JsonObject.class);
        if (purchaseToken == null) {
            return;
        }
        purchaseToken.addProperty("user_key", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key"));
        Api.getDefault().sendPayData(AESUtil.encode(AESUtil.toGson()), purchaseToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(
                        new Observer<RespDTO<JsonObject>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(RespDTO<JsonObject> jsonObject) {

                                if (jsonObject.code == 200) {
                                    String user_key = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key");
                                    if (!TextUtils.isEmpty(user_key)) {
                                        SPUtils.setSharedStringData(BaseApplication.getAppContext(), user_key, "");
                                    }
                                    RxBus.getInstance().post(new RxBean(MyConfig.PAY_STATUS_SUCCESS));
                                } else {
//                            LogUtils.loge("上报的json,报错:" + jsonObject.get("message").getAsString());
                                    afterFailed(json);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                afterFailed(json);
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );


    }

    private static void afterFailed(String json) {
        String user_key = SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key");
        if (!TextUtils.isEmpty(user_key)) {
            SPUtils.setSharedStringData(BaseApplication.getAppContext(), user_key, json);
            ToastUitl.showShort(R.string.tip_fail_report);
            RxBus.getInstance().post(new RxBean(MyConfig.SEND_STATUS_FAILED));
        }
    }
    /**
     * 保存数据到数据库（已消费的数据）
     *
     * @param purchaseHistory 购买记录
     */
//    private void savePurchaseHistory(final PurchaseHistoryBean purchaseHistory, final boolean needToast) {
//        PurchaseHistoryResponstory.insert(purchaseHistory, new DBExecuteCallback() {
//            @Override
//            public void onSuccess() {
//                reportServer(purchaseHistory, needToast);
//            }
//
//            @Override
//            public void onError() {
//                reportServer(purchaseHistory, needToast);
//            }
//        });
//    }

    /**
     * 查询最近的购买交易
     * 使用 Google Play 商店应用的缓存，而不发起网络请求
     * 建议在应用启动时和 onResume() 方法中调用 至少调用两次
     */
    public static void queryPurchase() {
        //        支付成功了，但是没有消费成功
        mBillingClient.queryPurchasesAsync("inapp", new PurchasesResponseListener() {
            public void onQueryPurchasesResponse(BillingResult param1BillingResult, List<Purchase> param1List) {
                // 查询成功且列表不为空
                if (param1BillingResult.getResponseCode() == 0 && !param1List.isEmpty()) {
                    Iterator<Purchase> iterator = param1List.iterator();
                    while (iterator.hasNext())
                        PayUtils.handlePurchase(iterator.next());
                }
            }
        });
    }

    /**
     * 查询列表，上传服务器
     */
    private void queryPurchaseHistory() {
//        消费成功了，但是通知服务器失败了，
//        PurchaseHistoryResponstory.queryList(new DefaultDBResultCallback<PurchaseHistoryBean>() {
//            @Override
//            public void onSuccess(List<PurchaseHistoryBean> purchaseHistoryBeans) {
//                super.onSuccess(purchaseHistoryBeans);
//                for (PurchaseHistoryBean purchaseHistory : purchaseHistoryBeans) {
//                    //上传自己的服务器
//                    reportServer(purchaseHistory, false);
//                }
//            }
//        });
    }

    //    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public static void destroy() {
        if (mBillingClient != null) {
            LogUtils.loge("BillingClient can only be used once -- closing connection");
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            if (mBillingClient.isReady()) {
                mBillingClient.endConnection();
            }
            mBillingClient = null;
        }
    }

    public static void checkPay() {


        Api.getDefault().checkPay(AESUtil.encode(AESUtil.toGson()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(
                        new Observer<CheckBean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(CheckBean checkBean) {
                                if (checkBean.code == 200) {
                                    CheckBean.DataBean jsonObject1 = checkBean.getData();
                                    if (jsonObject1 != null) {
                                        SPUtils.setSharedStringData(BaseApplication.getAppContext(), "first_name", jsonObject1.getFirst_name());
                                        SPUtils.setSharedStringData(BaseApplication.getAppContext(), "last_name", jsonObject1.getLast_name());

                                        CheckBean.DataBean.ServiceBean serviceBean = jsonObject1.getService();
                                        if (serviceBean != null) {
                                            SPUtils.setSharedIntData(BaseApplication.getAppContext(), "status", serviceBean.getStatus());
                                            SPUtils.setSharedBooleanData(BaseApplication.getAppContext(), "is_lifetime", serviceBean.isIs_lifetime());
                                            SPUtils.setSharedStringData(BaseApplication.getAppContext(), "valid_until", serviceBean.isIs_lifetime() ? BaseApplication.getAppContext().getResources().getString(R.string.date_forever) : serviceBean.getValid_until());
                                        }
                                        CheckBean.DataBean.UsageRecordBean usageRecord = jsonObject1.getUsageRecord();
                                        if (usageRecord != null) {
                                            SPUtils.setSharedIntData(BaseApplication.getAppContext(), "times", usageRecord.getTimes());
                                        }
                                    }
                                } else {
                                    ToastUitl.showShort(checkBean.message);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
//                        e.printStackTrace();
                                RxBus.getInstance().post(new RxBean(MyConfig.USER_STATUS_SUCCESS));
                            }

                            @Override
                            public void onComplete() {
                                RxBus.getInstance().post(new RxBean(MyConfig.USER_STATUS_SUCCESS));
                            }
                        }
                );


    }


    public static void postTimes(final int times) {

        SPUtils.setSharedIntData(BaseApplication.getAppContext(), "times", times);
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("times", times);
            jsonObject.addProperty("user_key", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key"));

            Api.getDefault().postTimes(AESUtil.encode(AESUtil.toGson()), jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(RxAdapter.exceptionTransformer())
                    .subscribe(
                            new Observer<RespDTO<JsonObject>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(RespDTO<JsonObject> jsonObject) {
                                    if (jsonObject.code == 200) {
                                        SPUtils.setSharedIntData(BaseApplication.getAppContext(), "times", times);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface CheckPayLister {

        void onError(String debugMessage);

        void onSucess();

    }

    /**
     * 调起微信支付的方法
     **/
    public static void toWXPay(final WechatBean.DataBean wechatBean) {

        final IWXAPI iwxapi = WXAPIFactory.createWXAPI(BaseApplication.getAppContext(), MyConfig.APP_ID); //初始化微信api
        iwxapi.registerApp(MyConfig.APP_ID); //注册appid
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) {

                PayReq request = new PayReq(); //调起微信的对象
                //这几个参数的值，正是上面我们说的统一下单接口后返回来的字段，我们对应填上去即可
                request.appId = wechatBean.getAppid();
                request.partnerId = wechatBean.getMch_id();
                request.prepayId = wechatBean.getPrepay_id();
                request.packageValue = WECHAT_PACKAGE;
                request.nonceStr = wechatBean.getNonce_str();
                request.timeStamp = wechatBean.getTimestamp();
                request.sign = wechatBean.getSign();
                iwxapi.sendReq(request);//发送调起微信的请求
                emitter.onComplete();

            }

        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Void aVoid) {

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

    //订阅
    public static void toWeChatScan(Activity context, String url) {
        try {
            WeChatPayUtil.pay(context, url);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            ToastUitl.showShort(R.string.faild_to_wechat);
        }
    }

    public static void unifiedOrder(@NonNull final String purchaseId, Context mContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("product_id", purchaseId);
        jsonObject.addProperty("user_key", SPUtils.getSharedStringData(BaseApplication.getAppContext(), "user_key"));

        Api.getDefault().unifiedOrder(AESUtil.encode(AESUtil.toGson()), jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxAdapter.exceptionTransformer())
                .subscribe(
                        new RxSubscriber<WechatBean>(mContext) {

                            @Override
                            protected void _onNext(WechatBean wechatBean) {
                                if (wechatBean.getCode() == 200 && wechatBean.getData() != null) {
                                    toWXPay(wechatBean.getData());
                                }
                            }

                            @Override
                            protected void _onError(int code, String message) {
                                ToastUitl.showShort(message);
                            }
                        }
                );
    }

    public static void buildDialog(Context context, DialogInterface.OnClickListener positivelister, DialogInterface.OnClickListener negativelister
    ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getResources().getString(R.string.dialog_tip1)).setMessage(context.getResources().getString(R.string.dialog_tip2)).setPositiveButton(context.getResources().getString(R.string.dialog_tip3), positivelister)
                .setCancelable(true);
        if (negativelister != null) {
            builder.setNegativeButton("取消", negativelister);
        }

        builder.show();

    }

    public static void buildDialog(Context context, String title, String str, DialogInterface.OnClickListener positivelister, DialogInterface.OnClickListener negativelister
    ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title).setMessage(str).setPositiveButton(context.getResources().getString(R.string.dialog_tip3), positivelister)
                .setCancelable(true);
        if (negativelister != null) {
            builder.setNegativeButton("取消", negativelister);
        }

        builder.show();

    }

}
