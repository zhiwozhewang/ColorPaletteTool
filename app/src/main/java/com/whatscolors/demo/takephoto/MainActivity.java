package com.whatscolors.demo.takephoto;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.utils.RequestPermissionCallBack;

import java.io.File;


public class MainActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MyImView.DrawLister {
    private MyImView photo;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    SeekBar seekBarone, seekbartwo, seekbarthree;
    float one = 1, two = 1, three = 1;
    private Bitmap imBaseBitmap, baseBitmap, copyBitmap;
    private Bitmap bitmap0;


    @Override
    protected void rxBusCallBack(RxBean rxBean) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void initView() {
        Button btnTakePhoto = (Button) findViewById(R.id.take_photo);
        Button btnTakeGallery = (Button) findViewById(R.id.take_gallery);
        Button savephoto = (Button) findViewById(R.id.save_photo);

        photo = (MyImView) findViewById(R.id.photo);
        photo.setDrawLister(this);
        btnTakePhoto.setOnClickListener(this);
        btnTakeGallery.setOnClickListener(this);
        savephoto.setOnClickListener(this);
        //
        seekBarone = (SeekBar) findViewById(R.id.one);
        seekbartwo = (SeekBar) findViewById(R.id.two);
        seekbarthree = (SeekBar) findViewById(R.id.three);
        seekBarone.setOnSeekBarChangeListener(this);
        seekbartwo.setOnSeekBarChangeListener(this);
        seekbarthree.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.save_photo:
                saveBitmap();
                break;
            case R.id.take_photo:
                requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                    @Override
                    public void granted() {
                        if (hasSdcard()) {
                            imageUri = Uri.fromFile(fileUri);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                //通过FileProvider创建一个content类型的Uri
                                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.whatscolors.demo.takephoto.fileprovider", fileUri);
                            PhotoUtils.takePicture(MainActivity.this, imageUri, CODE_CAMERA_REQUEST);
                        } else {
                            Toast.makeText(MainActivity.this, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
                            Log.e("asd", "设备没有SD卡");
                        }
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(MainActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.take_gallery:
                requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                    @Override
                    public void granted() {
                        PhotoUtils.openPic(MainActivity.this, CODE_GALLERY_REQUEST);
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(MainActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    private void saveBitmap() {

        imBaseBitmap = null;
        photo.setImageBitmap(getBitmap());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
//                    cropImageUri = Uri.fromFile(fileCropUri);
//                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    //新
                    bitmap = PhotoUtils.getBitmapFromUri(imageUri, this);

                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (hasSdcard()) {
//                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                            newUri = FileProvider.getUriForFile(this, "com.whatscolors.demo.takephoto.fileprovider", new File(newUri.getPath()));
//                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                        //新
                        bitmap = PhotoUtils.getBitmapFromUri(newUri, this);

                    } else {
                        Toast.makeText(MainActivity.this, "设备没有SD卡!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    break;
            }
            showImages(bitmap);
        }
    }


    private void showImages(Bitmap bitmap) {
        if (bitmap == null) {
            findViewById(R.id.li).setVisibility(View.INVISIBLE);
            return;
        }
//        if (baseBitmap != null && !baseBitmap.isRecycled()) {
//            baseBitmap.recycle();
//            baseBitmap = null;
//        }
//        if (copyBitmap != null && !copyBitmap.isRecycled()) {
//            copyBitmap.recycle();
//            copyBitmap = null;
//        }
//        baseBitmap = bitmap;


//        imBaseBitmap = bitmap;


//
        photo.setImageBitmap(bitmap);
//        //既然是复制一张与原图一模一样的图片那么这张复制图片的画纸的宽度和高度以及分辨率都要与原图一样,copyBitmap就为一张与原图相同尺寸分辨率的空白画纸
//        copyBitmap = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), baseBitmap.getConfig());
//        findViewById(R.id.li).setVisibility(View.VISIBLE);
        imBaseBitmap = null;
    }

    private Bitmap getBitmap() {
        //方法一获取imageview bitmap

//        Drawable drawable = photo.getDrawable();
//        Bitmap bitmap = Bitmap.createBitmap(
//                drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight(),
//                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                        : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        //canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
        //方法二获取imageview bitmap
//        BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
//        Bitmap bm = drawable.getBitmap();
//        Bitmap bitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);


        //截取imageview bitmap

        if (imBaseBitmap != null) {
            return imBaseBitmap;
        }
        View dView = photo;
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        imBaseBitmap = Bitmap.createBitmap(dView.getDrawingCache());
        dView.setDrawingCacheEnabled(false);

        return imBaseBitmap;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.one:
                one = progress * 1.0f / seekBar.getMax() * 360;
                break;
            case R.id.two:
                two = progress * 1.0f / (seekBar.getMax() - 50);
                break;
            case R.id.three:
                three = progress * 1.0f / (seekBar.getMax() - 50);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void afterDrawPath(MyImView myImView, Bitmap bitmap) {
        findViewById(R.id.li).setVisibility(View.VISIBLE);

    }


//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.one:
//                one = progress * 1.0f / seekBar.getMax() * 360;
//                break;
//            case R.id.two:
//                two = progress * 1.0f / (seekBar.getMax() - 50);
//                break;
//            case R.id.three:
//                three = progress * 1.0f / (seekBar.getMax() - 50);
//                break;
//        }
////        Paint paint = new Paint();
////        Canvas canvas = new Canvas(copyBitmap);
//        ColorMatrix colorMatrixS = new ColorMatrix();
//        colorMatrixS.setRotate(0, one);
//        colorMatrixS.setRotate(1, one);
//        colorMatrixS.setRotate(2, one);
//        ColorMatrix colorMatrixL = new ColorMatrix();
//        colorMatrixL.setScale(two, two, two, 1);
//        ColorMatrix colorMatrixB = new ColorMatrix();
//        colorMatrixB.setSaturation(three);
//        ColorMatrix colorMatriximg = new ColorMatrix();
//        //        通过postConcat()方法可以将以上效果叠加到一起
//        colorMatriximg.postConcat(colorMatrixB);
//        colorMatriximg.postConcat(colorMatrixL);
//        colorMatriximg.postConcat(colorMatrixS);
//        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatriximg);
////        paint.setColorFilter(colorMatrixColorFilter);
////        canvas.drawBitmap(baseBitmap, new Matrix(), paint);
//
////        photo.setDrawingCacheEnabled(true);
////        bitmap0 = Bitmap.createBitmap(photo.getDrawingCache());
////        photo.setDrawingCacheEnabled(false);
//
//        photo.drawBitmap(getBitmap(), colorMatrixColorFilter);
////        photo.setImageBitmap(copyBitmap);
//    }


}
