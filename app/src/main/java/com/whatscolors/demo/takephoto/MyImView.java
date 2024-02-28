package com.whatscolors.demo.takephoto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class MyImView extends ImageView {
    private boolean isUp = false;
    private boolean isProgressChanged = false;
    private int downX;
    private int downY;
    private boolean isMoveMode;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private GraphicPath mGraphicPath = new GraphicPath();
    private boolean isButtonClicked;
    private Paint markPaint, pathPaint, cutPaint;
    private int markedColor = Color.parseColor("#1a000000");
    private float strokeWidth = 3;
    private DrawLister drawLister;
    Bitmap baseBitmap;
    Matrix matrix;
    Paint paint;
    ColorMatrixColorFilter colorMatrixColorFilter;


    public MyImView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public void setDrawLister(DrawLister drawLister) {
        this.drawLister = drawLister;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isProgressChanged = false;
                isUp = false;
                downX = x;
                downY = y;
                isMoveMode = false;
                startX = (int) event.getX();
                startY = (int) event.getY();
                endX = startX;
                endY = startY;
                mGraphicPath.clear();
                mGraphicPath.addPath(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isButtonClicked) {
                    break;
                }
                mGraphicPath.addPath(x, y);
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                mGraphicPath.addPath(x, y);
                break;
            case MotionEvent.ACTION_CANCEL:
                isUp = true;
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //draw unmarked
//        canvas.drawRect(0, 0, width, height, pathPaint);
        if (isUp) {
            if (isProgressChanged) {
                //更新bitmap
                initbitmap(baseBitmap, canvas, colorMatrixColorFilter);
            } else {
                Path path = new Path();
                if (mGraphicPath.size() > 1) {
                    path.moveTo(mGraphicPath.pathX.get(0), mGraphicPath.pathY.get(0));
                    for (int i = 1; i < mGraphicPath.size(); i++) {
                        path.lineTo(mGraphicPath.pathX.get(i), mGraphicPath.pathY.get(i));
                    }
                } else {
                    return;
                }
                canvas.drawPath(path, pathPaint);
//                backBitmap();

            }

        } else {
            if (mGraphicPath.size() > 1) {
                for (int i = 1; i < mGraphicPath.size(); i++) {
                    canvas.drawLine(mGraphicPath.pathX.get(i - 1), mGraphicPath.pathY.get(i - 1), mGraphicPath.pathX.get(i), mGraphicPath.pathY.get(i), markPaint);
                }
            }
        }

    }

    public void initPaint() {
        markPaint = new Paint();
        markPaint.setColor(Color.WHITE);
        markPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        markPaint.setColor(Color.WHITE);
        markPaint.setStrokeWidth(strokeWidth);
        markPaint.setAntiAlias(true);
        //
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeWidth(strokeWidth);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pathPaint.setColor(markedColor);
        //
        cutPaint = new Paint();
        cutPaint.setAntiAlias(true);
        cutPaint.setStrokeWidth(strokeWidth);
        cutPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cutPaint.setColor(Color.WHITE);

    }


    private void initbitmap(Bitmap bitmap, Canvas canvas1, ColorMatrixColorFilter colorMatrixColorFilter) {
        if (bitmap == null) {
            bitmap = getBitmap();
        }

        if (bitmap == null || mGraphicPath.size() < 1)
            return;

        Rect mRect = new Rect(mGraphicPath.getLeft(), mGraphicPath.getTop(), mGraphicPath.getRight(), mGraphicPath.getBottom());
        if (mRect.left < 0)
            mRect.left = 0;
        if (mRect.right < 0)
            mRect.right = 0;
        if (mRect.top < 0)
            mRect.top = 0;
        if (mRect.bottom < 0)
            mRect.bottom = 0;
        int cut_width = Math.abs(mRect.left - mRect.right);
        int cut_height = Math.abs(mRect.top - mRect.bottom);
        if (cut_width > 0 && cut_height > 0) {
            Bitmap cutBitmap = Bitmap.createBitmap(bitmap, mRect.left, mRect.top, cut_width, cut_height);
            //上面是将全屏截图的结果先裁剪成需要的大小，下面是裁剪成曲线图形区域
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(Color.WHITE);
            if (colorMatrixColorFilter != null) {
                paint.setColorFilter(colorMatrixColorFilter);
            }
            //
            Bitmap temp = Bitmap.createBitmap(cut_width, cut_height, Bitmap.Config.ARGB_8888);
            Canvas canvas0 = new Canvas(temp);
            Path path = new Path();
            if (mGraphicPath.size() > 1) {
                path.moveTo((float) ((mGraphicPath.pathX.get(0) - mRect.left)), (float) ((mGraphicPath.pathY.get(0) - mRect.top)));
                for (int i = 1; i < mGraphicPath.size(); i++) {
                    path.lineTo((float) ((mGraphicPath.pathX.get(i) - mRect.left)), (float) ((mGraphicPath.pathY.get(i) - mRect.top)));
                }
            } else {
                return;
            }
            canvas0.drawPath(path, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // 关键代码，关于Xfermode和SRC_IN请自行查阅
//            Rect mRect0 = new Rect(0, 0, cutBitmap.getWidth(), cutBitmap.getHeight());
            canvas0.drawBitmap(cutBitmap, 0, 0, paint);
            canvas1.drawBitmap(temp, null, mRect, cutPaint);

        }
    }


    private void backBitmap() {
        if (mGraphicPath.size() < 1)
            return;

        Rect mRect = new Rect(mGraphicPath.getLeft(), mGraphicPath.getTop(), mGraphicPath.getRight(), mGraphicPath.getBottom());
        if (mRect.left < 0)
            mRect.left = 0;
        if (mRect.right < 0)
            mRect.right = 0;
        if (mRect.top < 0)
            mRect.top = 0;
        if (mRect.bottom < 0)
            mRect.bottom = 0;
        int cut_width = Math.abs(mRect.left - mRect.right);
        int cut_height = Math.abs(mRect.top - mRect.bottom);
        if (cut_width > 0 && cut_height > 0) {
            Bitmap cutBitmap = Bitmap.createBitmap(getBitmap(), mRect.left, mRect.top, cut_width, cut_height);
            //上面是将全屏截图的结果先裁剪成需要的大小，下面是裁剪成曲线图形区域
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(Color.WHITE);
            Bitmap temp = Bitmap.createBitmap(cut_width, cut_height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(temp);
            Path path = new Path();
            if (mGraphicPath.size() > 1) {
                path.moveTo((float) ((mGraphicPath.pathX.get(0) - mRect.left)), (float) ((mGraphicPath.pathY.get(0) - mRect.top)));
                for (int i = 1; i < mGraphicPath.size(); i++) {
                    path.lineTo((float) ((mGraphicPath.pathX.get(i) - mRect.left)), (float) ((mGraphicPath.pathY.get(i) - mRect.top)));
                }
            }
            canvas.drawPath(path, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // 关键代码，关于Xfermode和SRC_IN请自行查阅
            canvas.drawBitmap(cutBitmap, 0, 0, paint);
            if (drawLister != null && !isProgressChanged) {
                drawLister.afterDrawPath(this, cutBitmap);
            }
        }

    }

    public void drawBitmap(Bitmap baseBitmap, ColorMatrixColorFilter colorMatrixColorFilter) {
        if (colorMatrixColorFilter == null) {
            return;
        }
        this.baseBitmap = baseBitmap;
        this.colorMatrixColorFilter = colorMatrixColorFilter;
        isProgressChanged = true;
        postInvalidate();

    }

    public void drawBitmap(ColorMatrixColorFilter colorMatrixColorFilter) {
        this.drawBitmap(null, colorMatrixColorFilter);
    }

    private Bitmap getBitmap() {
        //截取imageview bitmap
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap imBaseBitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return imBaseBitmap;
    }

    public interface DrawLister {
        void afterDrawPath(MyImView myImView, Bitmap bitmap);
    }
}
