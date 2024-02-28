package com.whatscolors.demo.utils;

import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.api.RxSubscriber;
import com.whatscolors.demo.base.BaseApplication;
import com.whatscolors.demo.bean.DownloadInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.whatscolors.demo.MyConfig.gifname;
import static com.whatscolors.demo.utils.Utils.getFilePath;

/**
 * Author:      wangwei
 * Date:        2020/11/11 10:43
 * Version:
 */
public class DownLoadUtils {
    /**
     * 下载文件法1(使用Handler更新UI)
     *
     * @param destDir    下载目录
     * @param fileName   文件名
     * @param gifversion
     */
    public static void downloadFile(String url, final String destDir, final String fileName, final int gifversion) {
        Api.getDefault().downloadGif(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        InputStream inputStream = null;
//                        long total = 0;
//                        long responseLength;
                        FileOutputStream fos = null;
                        try {
                            byte[] buf = new byte[2048];
                            int len;
//                            responseLength = responseBody.contentLength();
                            inputStream = responseBody.byteStream();
                            final File file = new File(destDir, fileName);
                            File dir = new File(destDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            } else
                                dir.delete();

                            fos = new FileOutputStream(file);
//                            int progress = 0;
//                            int lastProgress;
//                            long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                            while ((len = inputStream.read(buf)) != -1) {
                                fos.write(buf, 0, len);
//                                total += len;
//                                lastProgress = progress;
//                                progress = (int) (total * 100 / responseLength);
//                                long curTime = System.currentTimeMillis();
//                                long usedTime = (curTime - startTime) / 1000;
//                                if (usedTime == 0) {
//                                    usedTime = 1;
//                                }
//                                long speed = (total / usedTime); // 平均每秒下载速度

                            }
                            fos.flush();
                            SPUtils.setSharedIntData(BaseApplication.getAppContext(), "gifversion", gifversion);
                        } catch (final Exception e) {
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {//new Consumer<Throwable>
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {// new Action()

                    }
                });
    }

    /**
     * 下载文件法2(使用RXJava更新UI)
     *
     * @param destDir
     * @param fileName
     */
    public static void downloadFile2(String url, final String destDir, final String fileName) {

        final DownloadInfo downloadInfo = new DownloadInfo();
        Api.getDefault().downloadGif(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .flatMap(new Function<ResponseBody, ObservableSource<DownloadInfo>>() {

                    @Override
                    public ObservableSource<DownloadInfo> apply(final ResponseBody responseBody) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<DownloadInfo>() {
                            @Override
                            public void subscribe(ObservableEmitter<DownloadInfo> emitter) throws Exception {
                                InputStream inputStream = null;
                                long total = 0;
                                long responseLength = 0;
                                FileOutputStream fos = null;
                                try {
                                    byte[] buf = new byte[2048];
                                    int len = 0;
                                    responseLength = responseBody.contentLength();
                                    inputStream = responseBody.byteStream();
                                    final File file = new File(destDir, fileName);
                                    downloadInfo.setFile(file);
                                    downloadInfo.setFileSize(responseLength);
                                    File dir = new File(destDir);
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }
                                    fos = new FileOutputStream(file);
                                    int progress = 0;
                                    int lastProgress = 0;
                                    long startTime = System.currentTimeMillis(); // 开始下载时获取开始时间
                                    while ((len = inputStream.read(buf)) != -1) {
                                        fos.write(buf, 0, len);
                                        total += len;
                                        lastProgress = progress;
                                        progress = (int) (total * 100 / responseLength);
                                        long curTime = System.currentTimeMillis();
                                        long usedTime = (curTime - startTime) / 1000;
                                        if (usedTime == 0) {
                                            usedTime = 1;
                                        }
                                        long speed = (total / usedTime); // 平均每秒下载速度
                                        // 如果进度与之前进度相等，则不更新，如果更新太频繁，则会造成界面卡顿
                                        if (progress > 0 && progress != lastProgress) {
                                            downloadInfo.setSpeed(speed);
                                            downloadInfo.setProgress(progress);
                                            downloadInfo.setCurrentSize(total);
                                            emitter.onNext(downloadInfo);
                                        }
                                    }
                                    fos.flush();
                                    downloadInfo.setFile(file);
                                    emitter.onComplete();
                                } catch (Exception e) {
                                    downloadInfo.setErrorMsg(e);
                                    emitter.onError(e);
                                } finally {
                                    try {
                                        if (fos != null) {
                                            fos.close();
                                        }
                                        if (inputStream != null) {
                                            inputStream.close();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<DownloadInfo>(BaseApplication.getAppContext()) {
                    @Override
                    protected void _onNext(DownloadInfo downloadInfo) {

                    }

                    @Override
                    protected void _onError(int code, String message) {

                    }
                });
    }

    public static boolean isGifFileHas() {
        File file = new File(getFilePath(), gifname);
        if (file.exists()) {
            return true;
        }
        return false;

    }

    public static String getGifPath() {

        return getFilePath() + "/" + gifname;

    }
}
