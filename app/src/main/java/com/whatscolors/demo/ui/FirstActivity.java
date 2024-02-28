package com.whatscolors.demo.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.whatscolors.demo.MyConfig;
import com.whatscolors.demo.api.Api;
import com.whatscolors.demo.base.BaseActivity;
import com.whatscolors.demo.bean.RxBean;
import com.whatscolors.demo.takephoto.PhotoUtils;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.takephoto.ShareUtil;
import com.whatscolors.demo.utils.AESUtil;
import com.whatscolors.demo.utils.GlideUtils;
import com.whatscolors.demo.utils.LogUtils;
import com.whatscolors.demo.utils.OpenCvUtil;
import com.whatscolors.demo.utils.PayUtils;
import com.whatscolors.demo.utils.RequestPermissionCallBack;
import com.whatscolors.demo.utils.SPUtils;
import com.whatscolors.demo.utils.ToastUitl;
import com.whatscolors.demo.utils.UCropUtil;
import com.whatscolors.demo.utils.http.RespDTO;
import com.whatscolors.demo.utils.http.RxAdapter;
import com.yalantis.ucrop.UCrop;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.whatscolors.demo.utils.PayUtils.buildDialog;
import static com.whatscolors.demo.utils.Utils.isZh;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.FileProvider;


public class FirstActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.rb_1)
    RadioButton rb1;
    @Bind(R.id.rb_2)
    RadioButton rb2;
    @Bind(R.id.rb_3)
    RadioButton rb3;
    @Bind(R.id.fs_rg)
    RadioGroup fsRg;

    @Bind(R.id.ic2_im_l)
    ImageView ic2ImL;
    @Bind(R.id.ic2_im_r)
    ImageView ic2ImR;
    @Bind(R.id.im_light)
    ImageView imLight;
    @Bind(R.id.ic2_li1)
    LinearLayout ic2Li1;
    @Bind(R.id.ic2_li2)
    LinearLayout ic2Li2;
    @Bind(R.id.ic2_li3)
    LinearLayout ic2Li3;
    @Bind(R.id.ic2_bt_li)
    LinearLayout ic2BtLi;
    @Bind(R.id.ic3_im_l)
    ImageView ic3ImL;
    @Bind(R.id.ic3_im_r)
    ImageView ic3ImR;
    @Bind(R.id.ic3_text_l)
    TextView ic3TextL;
    @Bind(R.id.ic3_text_l_input)
    EditText ic3TextLInput;
    @Bind(R.id.ic3_iml_l_input)
    ImageView ic3ImlLInput;
    @Bind(R.id.ic3_sb_l)
    AppCompatSeekBar ic3SbL;
    @Bind(R.id.ic3_imr_l_input)
    ImageView ic3ImrLInput;
    @Bind(R.id.ic3_text_c)
    TextView ic3TextC;
    @Bind(R.id.ic3_text_c_input)
    EditText ic3TextCInput;
    @Bind(R.id.ic3_iml_c_input)
    ImageView ic3ImlCInput;
    @Bind(R.id.ic3_sb_c)
    AppCompatSeekBar ic3SbC;
    @Bind(R.id.ic3_imr_c_input)
    ImageView ic3ImrCInput;
    @Bind(R.id.ic3_text_h)
    TextView ic3TextH;
    @Bind(R.id.ic3_text_h_input)
    EditText ic3TextHInput;
    @Bind(R.id.ic3_iml_h_input)
    ImageView ic3ImlHInput;
    @Bind(R.id.ic3_sb_h)
    AppCompatSeekBar ic3SbH;
    @Bind(R.id.ic3_imr_h_input)
    ImageView ic3ImrHInput;
    @Bind(R.id.ic3_li1)
    LinearLayout ic3Li1;
    @Bind(R.id.ic3_li2)
    LinearLayout ic3Li2;
    @Bind(R.id.ic3_li3)
    LinearLayout ic3Li3;
    @Bind(R.id.ic3_bt_li)
    LinearLayout ic3BtLi;
    @Bind(R.id.ic_1)
    RelativeLayout ic1;
    @Bind(R.id.ic_2)
    RelativeLayout ic2;
    @Bind(R.id.ic_3)
    RelativeLayout ic3;
    @Bind(R.id.ic_4)
    RelativeLayout ic4;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    @Bind(R.id.ic_1_li1)
    LinearLayout ic1Li1;
    @Bind(R.id.ic_1_view)
    View ic1View;
    @Bind(R.id.ic_1_li2)
    LinearLayout ic1Li2;
    @Bind(R.id.ic_3_view)
    View ic3View;
    @Bind(R.id.ic4_im_l)
    ImageView ic4ImL;
    @Bind(R.id.ic4_im_r)
    ImageView ic4ImR;
    @Bind(R.id.ic4_li1)
    LinearLayout ic4Li1;
    @Bind(R.id.ic4_li2)
    LinearLayout ic4Li2;
    @Bind(R.id.ic4_li3)
    LinearLayout ic4Li3;
    @Bind(R.id.ic4_li4)
    LinearLayout ic4Li4;
    @Bind(R.id.ic4_bt_li)
    LinearLayout ic4BtLi;
    @Bind(R.id.ic4_lb_text)
    TextView ic4LbText;
    @Bind(R.id.ic4_lch_text)
    TextView ic4LchText;
    @Bind(R.id.ic2_light_text)
    TextView ic2LightText;
    @Bind(R.id.ic2_bottom_re)
    RelativeLayout ic2BottomRe;
    @Bind(R.id.ic1_im_my)
    ImageView ic1ImMy;
    //            正比
    @Bind(R.id.ic3_text_s_input)
    EditText ic3TextSInput;
    @Bind(R.id.ic3_iml_s_input)
    ImageView ic3ImlSInput;
    @Bind(R.id.ic3_sb_s)
    AppCompatSeekBar ic3SbS;
    @Bind(R.id.ic3_imr_s_input)
    ImageView ic3ImrSInput;
    //    反比
    @Bind(R.id.ic3_text_i_input)
    EditText ic3TextiInput;
    @Bind(R.id.ic3_iml_i_input)
    ImageView ic3ImliInput;
    @Bind(R.id.ic3_sb_i)
    AppCompatSeekBar ic3Sbi;
    @Bind(R.id.ic3_imr_i_input)
    ImageView ic3ImriInput;
    //
    @Bind(R.id.ic1_pay_tip)
    TextView ic1PayTip;
    @Bind(R.id.ic3_text_s)
    TextView ic3TextS;
    @Bind(R.id.ic_4_li)
    LinearLayout ic4Li;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private Uri imageUri, cropImageUri;
    public static final int REQUEST_CROP_IML = 0001, REQUEST_CROP_IMR = 0002;

    float one = 1, two = 1, three = 1;
    //    private Bitmap imBaseBitmap, copyBitmap;//, saveBitmap;
    private Uri resultUriL;
    private Uri resultUriR;
    private MyBottomSheetDialog myBottomSheetDialog;
    public Bitmap ic3bitmap;
    private double[] labdifference;
    //    private boolean isIc3bitmapChange = false;
    private List<SoftReference<Disposable>> linkedList = new ArrayList();
    private Uri saveuri;
    private int times, status;
    private static String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    LogUtils.loge("OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("mixed_sample");

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    //    private Bitmap ic3bitmap_double;
    private long nowSeekTime, oldSeekTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_first;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            LogUtils.loge("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            LogUtils.loge("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void initTips() {
        times = SPUtils.getSharedIntData(mContext, "times");
        status = SPUtils.getSharedIntData(mContext, "status");
        if (status == 1) {
            ic1PayTip.setVisibility(View.GONE);
        } else {
            initTipText();
            ic1PayTip.setVisibility(View.VISIBLE);
            if (status == 2 && !SPUtils.getSharedBooleanData(mContext, "first_tip")) {
                buildDialog(mContext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
                SPUtils.setSharedBooleanData(mContext, "first_tip", true);
            }
        }
    }

    @Override
    public void initView() {
        setAppScreenBrightness(255);
        ic3TextHInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (!TextUtils.isEmpty(editable.toString())) {
//                    int i = Integer.parseInt(editable.toString());
//                    if (i < -200) {
//                        i = -200;
//                    } else if (i > 200) {
//                        i = 200;
//                    }
//                    ic3SbH.setProgress(i);
//                }
            }
        });
        ic3TextLInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                int i = Integer.parseInt(editable.toString());
//                ic3SbL.setProgress(i);
            }
        });
        ic3TextCInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                int i = Integer.parseInt(editable.toString());
//                ic3SbC.setProgress(i);
            }
        });
        ic3TextSInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                int i = Integer.parseInt(editable.toString());
//                ic3SbS.setProgress(i);
            }
        });
        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb1.setChecked(true);
//
//        ic2ImL.setDrawLister(this);
//        ic2ImR.setDrawLister(this);
        //
        ic3SbL.setOnSeekBarChangeListener(this);
        ic3SbC.setOnSeekBarChangeListener(this);
        ic3SbH.setOnSeekBarChangeListener(this);
        ic3SbS.setOnSeekBarChangeListener(this);
        ic3Sbi.setOnSeekBarChangeListener(this);
        //
        ic2LightText.setText(SPUtils.getSharedStringData(mContext, "sp1_str") + " : " + SPUtils.getSharedStringData(mContext, "sp2_str"));
        if (" : ".equalsIgnoreCase(ic2LightText.getText().toString())) {
            ic2LightText.setText("other");
        }
        initPermission();
        PayUtils.checkPay();

    }


    void initPermission() {
        requestPermissions(this, permissions, new RequestPermissionCallBack() {
            @Override
            public void granted() {

            }

            @Override
            public void denied() {
                ToastUitl.showShort(R.string.tip_18);
            }
        });
    }

    @OnClick({R.id.ic3_imr_h_input, R.id.ic3_iml_h_input,
            R.id.ic3_imr_c_input, R.id.ic3_iml_c_input,
            R.id.ic3_imr_l_input, R.id.ic3_iml_l_input, R.id.ic2_im_r, R.id.ic2_im_l, R.id.ic_1_li2,
            R.id.ic_1_li1, R.id.ic2_li3, R.id.ic3_li3, R.id.ic2_li2,
            R.id.ic3_li2, R.id.ic2_li1, R.id.ic3_li1,
            R.id.ic4_li4, R.id.ic4_li2, R.id.ic4_li1,
            R.id.ic2_bottom_re, R.id.ic1_im_my,
            R.id.ic3_iml_s_input, R.id.ic3_imr_s_input,
            R.id.ic3_iml_i_input, R.id.ic3_imr_i_input,
            R.id.ic4_li3})
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ic4_li3:
                shareTo();
                break;
            case R.id.ic1_im_my:
                startActivity(MyActivity.class);
                break;
            case R.id.ic2_bottom_re:
                initDialog();
                break;
            case R.id.ic3_iml_s_input:
                initSbProgress(ic3SbS, false);
                break;
            case R.id.ic3_imr_s_input:
                initSbProgress(ic3SbS, true);
                break;
            case R.id.ic3_iml_i_input:
                initSbProgress(ic3Sbi, false);
                break;
            case R.id.ic3_imr_i_input:
                initSbProgress(ic3Sbi, true);
                break;
            case R.id.ic3_imr_h_input:
                initSbProgress(ic3SbH, true);
                break;
            case R.id.ic3_iml_h_input:
                initSbProgress(ic3SbH, false);

                break;
            case R.id.ic3_imr_c_input:
                initSbProgress(ic3SbC, true);


                break;
            case R.id.ic3_iml_c_input:
                initSbProgress(ic3SbC, false);

                break;
            case R.id.ic3_imr_l_input:
                initSbProgress(ic3SbL, true);

                break;
            case R.id.ic3_iml_l_input:
                initSbProgress(ic3SbL, false);

                break;
            case R.id.ic2_im_l:
                if (imageUri != null)
                    UCropUtil.startUCrop(this, imageUri, REQUEST_CROP_IML);
                break;
            case R.id.ic2_im_r:
                if (imageUri != null)
                    UCropUtil.startUCrop(this, imageUri, REQUEST_CROP_IMR);
                break;
            case R.id.ic2_li3:
//                if (ic3ImL.getDrawable() == null || ic3ImR.getDrawable() == null) {
//                    return;
//                }
                rb3.setChecked(true);
                break;
            case R.id.ic3_li3:
                checkIc4();
                break;
            case R.id.ic4_li1:
//                rb3.setChecked(true);
                ic1.setVisibility(View.GONE);
                ic2.setVisibility(View.GONE);
                ic3.setVisibility(View.VISIBLE);
                ic4.setVisibility(View.GONE);
                fsRg.setVisibility(View.VISIBLE);
                break;
            case R.id.ic4_li2:
                saveLi4();
                ToastUitl.showShort(R.string.tip_19);
                break;
            case R.id.ic4_li4:
                saveLi4();
                rb1.setChecked(true);
                break;
            case R.id.ic2_li2:
                //ic2 reset
                if (resultUriR != null || resultUriL != null) {
//                    ic2ImR.setImageBitmap(imBaseBitmap);
//                    ic2ImL.setImageBitmap(imBaseBitmap);
//                    ic3ImR.setImageBitmap(imBaseBitmap);
                    //                    initIc3ImR();
//                    ic3ImL.setImageBitmap(imBaseBitmap);
                    showImagesByUri(imageUri);
                    resultUriR = null;
                    resultUriL = null;
                } else {
                    ToastUitl.showShort(R.string.tip_20);

                }
                break;
            case R.id.ic3_li2:
                if (ic3SbL.getProgress() == 75 && ic3SbC.getProgress() == 75 && ic3SbS.getProgress() == 75 && ic3Sbi.getProgress() == 75 && ic3SbH.getProgress() == 200) {
                    ToastUitl.showShort(R.string.tip_20);
                    return;
                }
                ic3SbL.setProgress(75);
                ic3SbC.setProgress(75);
                ic3SbH.setProgress(200);
                ic3SbS.setTag(true);//reset
                ic3SbS.setProgress(75);
                ic3Sbi.setTag(true);//reset
                ic3Sbi.setProgress(75);
                GlideUtils.display(ic3ImR, resultUriR != null ? resultUriR : imageUri);
//                ic3ImR.setImageBitmap(ic3bitmap);
                cleanDisposable();
                break;
            case R.id.ic2_li1:
                rb1.setChecked(true);
                break;
            case R.id.ic3_li1:
                rb2.setChecked(true);
                break;
            case R.id.ic_1_li1:
                checkState(1);
                break;
            case R.id.ic_1_li2:

                checkState(2);

                break;

        }
    }

    private void toSecond(final int i) {
        if (status == 0 || status == 2) {
            if (times >= 10) {
                ToastUitl.showShort(R.string.tip_22);
                return;
            }
        }
        requestPermissions(this, permissions, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                if (i == 1) {
                    if (hasSdcard()) {
//                            PayUtils.checkPay();
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            //通过FileProvider创建一个content类型的Uri
                            imageUri = FileProvider.getUriForFile(FirstActivity.this, getPackageName() + ".fileprovider", fileUri);
                        PhotoUtils.takePicture(FirstActivity.this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUitl.showShort(R.string.tip_26);
                        Log.e("asd", "The device does not have an SD card");
                    }
                } else
                    PhotoUtils.openPic(FirstActivity.this, CODE_GALLERY_REQUEST);

            }

            @Override
            public void denied() {
                ToastUitl.showShort(R.string.tip_18);

            }
        });
    }

    private void checkClickNums() {
        if ((status == 0 || status == 2) && times < 10) {
            times++;
            initTipText();
            PayUtils.postTimes(times);
        }
    }

    private void initDialog() {
        if (myBottomSheetDialog == null) {

            myBottomSheetDialog = new MyBottomSheetDialog(mContext, null);
            myBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    ic2LightText.setText(SPUtils.getSharedStringData(mContext, "sp1_str") + " : " + SPUtils.getSharedStringData(mContext, "sp2_str"));
                }
            });

        }
//        new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View view, int newState) {
//                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
//                    //这里是bottomSheet状态的改变
//                    ic2LightText.setText(SPUtils.getSharedStringData(mContext, "sp1_str") + " : " + SPUtils.getSharedStringData(mContext, "sp2_str"));
//            }
//
//            @Override
//            public void onSlide(@NonNull View view, float v) {
//                //这里是拖拽中的回调，根据slideOffset可以做一些动画
//            }
//        }

        myBottomSheetDialog.show();
    }

    private void checkIc4() {
        ic4.setVisibility(View.VISIBLE);
        ic1.setVisibility(View.GONE);
        ic2.setVisibility(View.GONE);
        ic3.setVisibility(View.GONE);
        fsRg.setVisibility(View.INVISIBLE);

        ic4ImL.setImageBitmap(getBitmap(ic3ImL));
        ic4ImR.setImageBitmap(getBitmap(ic3ImR));

        ic4LbText.setText(ic2LightText.getText());
        ic4LchText.setText(getResources().getString(R.string.lch_string, ic3TextHInput.getText(), ic3TextCInput.getText(), ic3TextLInput.getText()));
    }

    private void initSbProgress(AppCompatSeekBar seekBar, boolean b) {
        if (b) {
            //加
            if (seekBar.getProgress() < seekBar.getMax()) {
                seekBar.setProgress(seekBar.getProgress() + 1);
            }

        } else {
            if (seekBar.getProgress() > 0) {//seekBar.getMin()
                seekBar.setProgress(seekBar.getProgress() - 1);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        TLogService.loge("MODEL-CHOOSE", "TLogService", "requestCode:" + requestCode + ";resultCode:" + resultCode + "data:" + (data == null ? "null" : data.getDataString()));
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    //新
//                    TLogService.loge("MODEL-CHOOSE", "TLogService", "拍照返回：" + imageUri);

//                    bitmap = PhotoUtils.getBitmapFromUri(imageUri, this);
                    if (imageUri == null) {
                        return;
                    }
                    rb2.setChecked(true);
                    showImagesByUri(imageUri);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (hasSdcard()) {
                        imageUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        // 新
//                        TLogService.loge("MODEL-CHOOSE", "TLogService", "相册返回：" + imageUri);

//                        bitmap = PhotoUtils.getBitmapFromUri(imageUri, this);
//                        LogUtils.loge("相册返回图片大小：" + bitmap.getByteCount());
                    } else {
                        ToastUitl.showShort(R.string.tip_26);
                    }
                    if (imageUri == null) {
                        return;
                    }
                    rb2.setChecked(true);
                    showImagesByUri(imageUri);
                    break;
                case CODE_RESULT_REQUEST:
//                    bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
//                    showImagesByUri(bitmap);
                    break;
                case REQUEST_CROP_IMR:

                    resultUriR = UCrop.getOutput(data);
                    GlideUtils.display(ic2ImR, resultUriR);
                    GlideUtils.display(ic3ImR, resultUriR);
//                    ic2ImR.setImageBitmap(PhotoUtils.getBitmapFromUri(resultUriR, this));
//                    ic3ImR.setImageBitmap(PhotoUtils.getBitmapFromUri(resultUriR, this));
                    initIc3ImR();
                    break;
                case REQUEST_CROP_IML:
                    resultUriL = UCrop.getOutput(data);
                    GlideUtils.display(ic2ImL, resultUriL);
                    GlideUtils.display(ic3ImL, resultUriL);
//                    ic2ImL.setImageBitmap(PhotoUtils.getBitmapFromUri(resultUriL, this));
//                    ic3ImL.setImageBitmap(PhotoUtils.getBitmapFromUri(resultUriL, this));
                    break;
                case UCrop.RESULT_ERROR:
                    final Throwable cropError = UCrop.getError(data);
                    break;
            }
        }
    }

    private void initIc3ImR() {
        ic3bitmap = null;
        saveuri = null;
        ic3SbL.setProgress(75);
        ic3SbC.setProgress(75);
        ic3SbH.setProgress(200);
        ic3SbS.setTag(true);
        ic3SbS.setProgress(75);
        ic3Sbi.setTag(true);
        ic3Sbi.setProgress(75);
        cleanDisposable();
    }

    private void showImages(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
//        TLogService.loge("MODEL-CHOOSE", "TLogService", "展示bitmap：" + bitmap.toString());

//
        rb2.setChecked(true);
        ic2ImL.setImageBitmap(bitmap);
        //        //既然是复制一张与原图一模一样的图片那么这张复制图片的画纸的宽度和高度以及分辨率都要与原图一样,copyBitmap就为一张与原图相同尺寸分辨率的空白画纸
//        Bitmap copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        ic2ImR.setImageBitmap(bitmap);
        //
        ic3ImL.setImageBitmap(bitmap);
//        ic3ImR.setImageBitmap(bitmap);
        initIc3ImR();
        //
//        imBaseBitmap = bitmap;
//        copyBitmap = null;
    }

    private void showImagesByUri(Uri uri) {
        if (uri == null) {
            return;
        }
//
        //        //既然是复制一张与原图一模一样的图片那么这张复制图片的画纸的宽度和高度以及分辨率都要与原图一样,copyBitmap就为一张与原图相同尺寸分辨率的空白画纸
//        Bitmap copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        GlideUtils.display(ic2ImL, uri);
        GlideUtils.display(ic2ImR, uri);
        //
        GlideUtils.display(ic3ImL, uri);
        GlideUtils.display(ic3ImR, uri);
        initIc3ImR();
        //
    }

    private Bitmap getBitmap(ImageView photo) {

        //截取imageview bitmap
//        View dView = photo;
//        dView.setDrawingCacheEnabled(true);
//        dView.buildDrawingCache();
//        Bitmap saveBitmap = Bitmap.createBitmap(dView.getDrawingCache());
//        dView.setDrawingCacheEnabled(false);
//        BitmapDrawable bitmap = (BitmapDrawable) photo.getDrawable();
//        saveBitmap = bitmap.getBitmap();
        return ((BitmapDrawable) photo.getDrawable()).getBitmap();
    }

    private Bitmap getIc4LiBitmap() {

        //  截取imageview bitmap
        ic4Li.setDrawingCacheEnabled(true);
        ic4Li.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(ic4Li.getDrawingCache());
        ic4Li.setDrawingCacheEnabled(false);

        return bitmap;

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
        nowSeekTime = System.currentTimeMillis();
        if (R.id.ic3_sb_i == seekBar.getId()) {

            Object object = ic3Sbi.getTag();
            if (object != null) {
                ic3Sbi.setTag(null);
                ic3TextiInput.setText((progress - 75) / 5f + "");//i
                return;
            }
            float ic3textsl = Float.parseFloat(ic3TextiInput.getText().toString());

            int oldic3SbSProgress = (int) (ic3textsl * 5 + 75);
            int nowic3SbSProgress = ic3Sbi.getProgress();
            int ic3SbSProgress = nowic3SbSProgress - oldic3SbSProgress;
            //
            int ic3SbcProgress = ic3SbC.getProgress() + (ic3SbSProgress);
            if (ic3SbcProgress < 0) {
                ic3SbcProgress = 0;
            } else if (ic3SbcProgress > 150) {
                ic3SbcProgress = 150;
            }
            ic3SbC.setProgress(ic3SbcProgress);
            int ic3SblProgress = ic3SbL.getProgress() - ic3SbSProgress;
            if (ic3SblProgress < 0) {
                ic3SblProgress = 0;
            } else if (ic3SblProgress > 150) {
                ic3SblProgress = 150;
            }
            ic3SbL.setProgress(ic3SblProgress);
            ic3TextiInput.setText((progress - 75) / 5f + "");//i
            return;
        }
        if (R.id.ic3_sb_s == seekBar.getId()) {

            Object object = ic3SbS.getTag();
            if (object != null) {
                ic3SbS.setTag(null);
                ic3TextSInput.setText((progress - 75) / 5f + "");//L
                return;
            }
            float ic3textsl = Float.parseFloat(ic3TextSInput.getText().toString());

            int oldic3SbSProgress = (int) (ic3textsl * 5 + 75);
            int nowic3SbSProgress = ic3SbS.getProgress();
            int ic3SbSProgress = nowic3SbSProgress - oldic3SbSProgress;
            //
            int ic3SbcProgress = ic3SbC.getProgress() + (ic3SbSProgress);
            if (ic3SbcProgress < 0) {
                ic3SbcProgress = 0;
            } else if (ic3SbcProgress > 150) {
                ic3SbcProgress = 150;
            }
            ic3SbC.setProgress(ic3SbcProgress);
            int ic3SblProgress = ic3SbL.getProgress() + ic3SbSProgress;
            if (ic3SblProgress < 0) {
                ic3SblProgress = 0;
            } else if (ic3SblProgress > 150) {
                ic3SblProgress = 150;
            }
            ic3SbL.setProgress(ic3SblProgress);
            ic3TextSInput.setText((progress - 75) / 5f + "");//L
            return;
        }

        if (labdifference == null) {
            labdifference = new double[3];
        }

        switch (seekBar.getId()) {

            case R.id.ic3_sb_h:
                ic3TextHInput.setText(((progress - 200) / 2f) + "");//L
                labdifference[0] = (progress - 200) / 2D;
                break;
            case R.id.ic3_sb_c:
                ic3TextCInput.setText((progress - 75) / 5f + "");//A
                labdifference[1] = (progress - 75) / 5D;// ;
                break;
            case R.id.ic3_sb_l:
                ic3TextLInput.setText((progress - 75) / 5f + "");//B
                labdifference[2] = (progress - 75) / 5D * -1;//* 0.51D
                break;

        }
        LogUtils.loge("labdifference[1]:" + labdifference[1] + ",labdifference[2]:" + labdifference[2]);

        if ((nowSeekTime - oldSeekTime) < 500) {
//            LogUtils.loge("www","seekbar时间差 : " + (nowSeekTime - oldSeekTime));
//            Log.e("www", "seekbar时间差 : " + (nowSeekTime - oldSeekTime));
//            oldSeekTime = System.currentTimeMillis();
            return;
        }
//        Log.e("www", "seekbar大于500的时间差 : " + (nowSeekTime - oldSeekTime));
        oldSeekTime = System.currentTimeMillis();
        initIc3ImRBitmap(labdifference);

    }


    private void initIc3ImRBitmap(final double[] labdifference) {


        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) {
//                LogUtils.loge("初始时间 : " + System.currentTimeMillis());
                Log.e("www", "初始时间 : " + System.currentTimeMillis());

                if (ic3bitmap == null) {
                    ic3bitmap = getBitmap(ic3ImR);//((BitmapDrawable) ic3ImR.getDrawable()).getBitmap();//;//
//                    isIc3bitmapChange = true;

                    //其实我们获得宽和高就是图片像素的宽和高
                    //它们的乘积就是总共一张图片拥有的像素点数
//                    int width = ic3bitmap.getWidth();
//                    int height = ic3bitmap.getHeight();
//                    if (mat != null) {
//                        mat.release();
//                    }
//                    mat = new Mat(width, height, CvType.CV_8UC4);
////                    mat = new Mat();
//                    Utils.bitmapToMat(ic3bitmap, mat);
                    // Bitmap.Config.RGB_565);
//                    ic3bitmap_double = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//                    cleanDisposable();
                }
//                Bitmap bitmap1 = OpenCvUtil.ImgaeToNegative4(ic3bitmap, ic3bitmap_double, labdifference);


                Bitmap bitmap1 = OpenCvUtil.ImgaeToNegative4(ic3bitmap, labdifference);//getBitmap(ic3ImR)
//                isIc3bitmapChange = false;
                if (bitmap1 != null) {
                    emitter.onNext(bitmap1);
                }
                emitter.onComplete();

            }

        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        linkedList.add(new SoftReference(d));
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        LogUtils.loge("结束时间 : " + System.currentTimeMillis());
                        ic3ImR.setImageBitmap(bitmap);
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

//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        switch (seekBar.getId()) {
//            case R.id.ic3_sb_l:
//                ic3TextLInput.setText(progress + "");
//                break;
//            case R.id.ic3_sb_c:
//                ic3TextCInput.setText(progress + "");
//                break;
//            case R.id.ic3_sb_h:
//                ic3TextHInput.setText(progress + "");
//                break;
//        }
////
//        //h(0-360),c(0-255),l(0-255)
//        HSL hsl = new HSL(ic3SbH.getProgress(), ic3SbC.getProgress(), ic3SbL.getProgress());
//        RGB rgb = Converter.HSL2RGB(hsl);
//        ColorMatrix colorMatrix = new ColorMatrix();
//        //设置偏移量
//        colorMatrix.setScale(caculate(rgb.red), caculate(rgb.green),
//                caculate(rgb.blue), 1);
////        //偏向颜色的值
//        Log.e("ww", "颜色值：#" + Integer.toHexString(rgb.red)
//                + Integer.toHexString(rgb.green)
//                + Integer.toHexString(rgb.blue)
//        );//+ Integer.toHexString(seekBarB.getProgress())
////
////        //用来显示偏向的颜色
////        colorView.setBackgroundColor(Color.argb(seekBarA.getProgress(),
////                seekBarR.getProgress(),
////                seekBarG.getProgress(),
////                seekBarB.getProgress()));
//
//
//        //为组件设置新的过滤器
//        ic3ImR.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//
//
//
//    }

    protected float caculate(int progress) {
        float scale = progress / 128f;
        return scale;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanDisposable();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rb_1:
                    ic1.setVisibility(View.VISIBLE);
                    ic2.setVisibility(View.GONE);
                    ic3.setVisibility(View.GONE);
                    ic4.setVisibility(View.GONE);
                    fsRg.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_2:
                    ic1.setVisibility(View.GONE);
                    ic2.setVisibility(View.VISIBLE);
                    ic3.setVisibility(View.GONE);
                    ic4.setVisibility(View.GONE);
                    fsRg.setVisibility(View.VISIBLE);

                    break;
                case R.id.rb_3:
                    ic1.setVisibility(View.GONE);
                    ic2.setVisibility(View.GONE);
                    ic3.setVisibility(View.VISIBLE);
                    ic4.setVisibility(View.GONE);
                    fsRg.setVisibility(View.VISIBLE);

                    break;

            }
        }

    }

    private void cleanDisposable() {
        if (!linkedList.isEmpty()) {
            for (SoftReference<Disposable> disposableSoftReference : linkedList) {
                Disposable disposable = disposableSoftReference.get();
                if (disposable != null && !disposable.isDisposed())
                    disposable.dispose();
            }
            linkedList.clear();
        }
    }

    private void saveLi4() {
        if (saveuri == null) {
            saveuri = PhotoUtils.saveBmp2Gallery(mContext, getIc4LiBitmap(), String.valueOf(System.currentTimeMillis() / 1000f));
            checkClickNums();
        }
    }

    private void shareTo() {
        saveLi4();
        ShareUtil.shareFile(mContext, saveuri);
    }

    private void initTipText() {
        String string;
        int index;
        if (isZh(mContext)) {
            string = Math.max(0, 10 - times) + "张图片";
            index = 6;
        } else {
            string = Math.max(0, 10 - times) + " pictures";
            index = 19;
        }

//            if ("google".equals(getString(R.string.str_pay))) {
//            string = Math.max(0, 10 - times) + " pictures";
//            index = 19;
//        } else {
//            string = Math.max(0, 10 - times) + "张图片";
//            index = 6;
//        }
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.tip_29, string));
        if (index + string.length() > spannableString.length() || index > spannableString.length()) {
            ic1PayTip.setText(spannableString);
            return;
        }
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF7B00"));
        spannableString.setSpan(colorSpan, index, index + string.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ic1PayTip.setText(spannableString);
    }


    @Override
    protected void rxBusCallBack(RxBean rxBean) {
        if (rxBean.payStatus == MyConfig.USER_STATUS_SUCCESS) {
            initTips();
        }
    }

    public void checkState(final int type) {
        Api.getDefault().checkState(AESUtil.encode(AESUtil.toGson()))
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
                                    toSecond(type);
                                } else
                                    showShortToast(jsonObject.message);

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
//                                toSecond(type);
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );

    }


}
