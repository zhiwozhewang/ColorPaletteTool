package com.whatscolors.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/*
 * 用来处理图片颜色的工具类
 */
public class ImageHelper {

    /**
     * 该方法根据色光三原色，改变图片颜色
     *
     * @param bmp        原图片
     * @param huge       色相
     * @param saturation 饱和度
     * @param lum        亮度
     * @return
     */
    public static Bitmap ImageUtil(Bitmap bmp, float huge, float saturation, float lum) {
        //注意，android不允许在原有的bitmap上操作，因此我们必须重画一个btimap来保存我们所做的操作并返回
        //第三个参数为制定颜色模式，通常会使用bitmap的最高处理方式
        Bitmap btp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(btp);//实例化一块画布
        Paint mPaint = new Paint();//实例化一支画笔
        mPaint.setStrokeWidth(Paint.ANTI_ALIAS_FLAG);//设置为抗锯齿

        //实例化处理色相的颜色矩阵
        ColorMatrix hugeMatrix = new ColorMatrix();
        hugeMatrix.setRotate(0, huge);//0表示红色
        hugeMatrix.setRotate(1, huge);//1表示设置绿色
        hugeMatrix.setRotate(2, huge);//2表示蓝色

        //实例化处理饱和度的矩阵
        ColorMatrix satMatrix = new ColorMatrix();
        //查看该方法的源码发现，只设置一个值方法内部就直接改变了每一个三原色的饱和度
        satMatrix.setSaturation(saturation);

        //实例化处理亮度的矩阵
        ColorMatrix lumMatrix = new ColorMatrix();
        //参数从左到右依次为红色亮度，绿色，蓝色，透明度（1表示完全不透明）
        lumMatrix.setScale(lum, lum, lum, 1);

        //再实例化一个颜色矩阵将上面的颜色设定都柔和再一起
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(hugeMatrix);
        imageMatrix.postConcat(satMatrix);
        imageMatrix.postConcat(lumMatrix);

        //将调好的颜色设置给画笔
        mPaint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        //然后我们用调整好的颜色画笔将原来的图片bmp画到新的bitmap上
        canvas.drawBitmap(bmp, 0, 0, mPaint);

        return btp;

    }

    /**
     * 将图片转换为负片
     *
     * @param bitmap 原来图片
     * @return 新图片
     */
    public static Bitmap ImgaeToNegative(Bitmap bitmap) {
        //其实我们获得宽和高就是图片像素的宽和高
        //它们的乘积就是总共一张图片拥有的像素点数
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];//用来存储旧的色素点的数组
        int[] newPx = new int[width * height];//用来存储新的像素点的数组
        int color;//用来存储原来颜色值
        int r, g, b, a;//存储颜色的四个分量：红，绿，蓝，透明度

        //该方法用来将图片的像素写入到oldPx中，我们这样子设置，就会获取全部的像素点
        //第一个参数为写入的数组，第二个参数为读取第一个的像素点的偏移量，一般设置为0
        //第三个参数为写入时，多少个像素点作为一行,第三个和第四个参数为读取的起点坐标
        //第五个参数表示读取的长度，第六个表示读取的高度
        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        //下面用循环来处理每一个像素点
        for (int i = 0; i < width * height; i++) {

            color = oldPx[i];//获取一个原来的像素点
            r = Color.red(color);//获取红色分量，下同
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //下面计算生成新的颜色分量
            r = 255 - r;
            g = 255 - g;
            b = 255 - b;

            //下面主要保证r g b 的值都必须在0~255之内
            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }
            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }
            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }

            //下面合成新的像素点，并添加到newPx中
            color = Color.argb(a, r, g, b);
            newPx[i] = color;
        }

        //然后重要的一步，为bmp设置新颜色了,该方法中的参数意义与getPixels中的一样
        //无非是将newPx写入到bmp中
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    /**
     * 将图片变成老照片
     *
     * @param bitmap 原来的图片
     * @return 新图片（即老照片）
     */
    public static Bitmap ImgaeToOld(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];//用来存储旧的色素点的数组
        int[] newPx = new int[width * height];//用来存储新的像素点的数组
        int color;//用来存储原来颜色值
        int r, g, b, a;//存储颜色的四个分量：红，绿，蓝，透明度

        //第一个参数为写入的数组，第二个参数为读取第一个的像素点的偏移量，一般设置为0
        //第三个参数为写入时，多少个像素点作为一行,第三个和第四个参数为读取的起点坐标
        //第五个参数表示读取的长度，第六个表示读取的高度
        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {

            color = oldPx[i];//获取一个原来的像素点
            r = Color.red(color);//获取红色分量，下同
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            //下面计算生成新的颜色分量
            r = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            g = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            b = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            //下面主要保证r g b 的值都必须在0~255之内
            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }
            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }
            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }

            //下面合成新的像素点，并添加到newPx中
            color = Color.argb(a, r, g, b);
            newPx[i] = color;
        }

        //然后重要的一步，为bmp设置新颜色了
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    /**
     * 将图片变成浮雕
     *
     * @param bitmap 旧照片
     * @return 新照片
     */
    public static Bitmap ImgaeToRelief(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];//用来存储旧的色素点的数组
        int[] newPx = new int[width * height];//用来存储新的像素点的数组
        int color;//用来存储前一个颜色值
        int r, g, b, a;//存储颜色的四个分量：红，绿，蓝，透明度
        int color1;//用来存储后一个颜色值
        int r1, g1, b1, a1;

        //第一个参数为写入的数组，第二个参数为读取第一个的像素点的偏移量，一般设置为0
        //第三个参数为写入时，多少个像素点作为一行,第三个和第四个参数为读取的起点坐标
        //第五个参数表示读取的长度，第六个表示读取的高度
        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        //注意是从1开始循环
        for (int i = 1; i < width * height; i++) {

            color = oldPx[i - 1];//获取前一个像素点
            r = Color.red(color);//获取红色分量，下同
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            color1 = oldPx[i];//获取后一个像素点
            r1 = Color.red(color1);//获取红色分量，下同
            g1 = Color.green(color1);
            b1 = Color.blue(color1);
            a1 = Color.alpha(color1);

            //下面计算生成新的颜色分量，注意浮雕是前一个像素减去后一个像素再加上127，赋给前一个
            r = r - r1 + 127;
            g = g - g1 + 127;
            b = b - b1 + 127;

            //下面主要保证r g b 的值都必须在0~255之内
            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }
            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }
            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }

            //下面合成新的像素点，并添加到newPx中
            color = Color.argb(a, r, g, b);
            newPx[i] = color;
        }

        //然后重要的一步，为bmp设置新颜色了
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

}