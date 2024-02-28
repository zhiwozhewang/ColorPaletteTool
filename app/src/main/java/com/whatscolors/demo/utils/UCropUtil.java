package com.whatscolors.demo.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;

import androidx.core.app.ActivityCompat;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

public class UCropUtil {

//
//    //设置Toolbar标题
//    void setToolbarTitle(@Nullable String text)
//    //设置裁剪的图片格式
//    void setCompressionFormat(@NonNull Bitmap.CompressFormat format)
//    //设置裁剪的图片质量，取值0-100
//    void setCompressionQuality(@IntRange(from = 0) int compressQuality)
//    //设置最多缩放的比例尺
//    void setMaxScaleMultiplier(@FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier)
//    //动画时间
//    void setImageToCropBoundsAnimDuration(@IntRange(from = 100) int durationMillis)
//    //设置图片压缩最大值
//    void setMaxBitmapSize(@IntRange(from = 100) int maxBitmapSize)
//    //是否显示椭圆裁剪框阴影
//    void setOvalDimmedLayer(boolean isOval)
//    //设置椭圆裁剪框阴影颜色
//    void setDimmedLayerColor(@ColorInt int color)
//    //是否显示裁剪框
//    void setShowCropFrame(boolean show)
//    //设置裁剪框边的宽度
//    void setCropFrameStrokeWidth(@IntRange(from = 0) int width)
//    //是否显示裁剪框网格
//    void setShowCropGrid(boolean show)
//    //设置裁剪框网格颜色
//    void setCropGridColor(@ColorInt int color)
//    //设置裁剪框网格宽
//    void setCropGridStrokeWidth(@IntRange(from = 0) int width)

    /**
     * 启动裁剪
     *
     * @param activity     上下文
     * @param sourceUri    需要裁剪图片的Uri
     * @param requestCode  比如：UCrop.REQUEST_CROP
     * @param aspectRatioX 裁剪图片宽高比
     * @param aspectRatioY 裁剪图片宽高比
     * @return
     */
    public static String startUCrop(Activity activity, Uri sourceUri, int requestCode, float aspectRatioX, float aspectRatioY) {
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, requestCode + "whatcolor_cut.jpg");
        if (outFile.exists()) {
            outFile.delete();
        }
        //裁剪后图片的绝对路径
        String cameraScalePath = outFile.getAbsolutePath();
        Uri destinationUri = Uri.fromFile(outFile);
        //初始化，第一个参数：需要裁剪的图片；第二个参数：裁剪后图片
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        //初始化UCrop配置
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.SCALE);
        //是否隐藏底部容器，默认显示
//        options.setHideBottomControls(true);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(activity, android.R.color.transparent));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(activity, android.R.color.transparent));
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(true);
        //UCrop配置
        uCrop.withOptions(options);
        //设置裁剪图片的宽高比，比如16：9
//        uCrop.withAspectRatio(aspectRatioX, aspectRatioY);
        uCrop.useSourceImageAspectRatio();
        //跳转裁剪页面
        uCrop.start(activity, requestCode == -1 ? UCrop.REQUEST_CROP : requestCode);
        return cameraScalePath;
    }

    public static String startUCrop(Activity activity, String sourceFilePath) {
        Uri sourceUri = Uri.fromFile(new File(sourceFilePath));
        return startUCrop(activity, sourceUri, UCrop.REQUEST_CROP, 16, 9);
    }

    public static String startUCrop(Activity activity, Uri sourceUri, int requestCode) {

        return startUCrop(activity, sourceUri, requestCode, 16, 9);
    }

}
