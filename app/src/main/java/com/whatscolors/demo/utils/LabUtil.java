package com.whatscolors.demo.utils;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.core.graphics.ColorUtils;

public class LabUtil {


    private static Bitmap bmp;

    private static int[] LABToRGB(double[] lab) {
        int[] outLab = new int[3];

        int color = ColorUtils.LABToColor(lab[0], lab[1], lab[2]);
        outLab[0] = Color.red(color);
        outLab[1] = Color.green(color);
        outLab[2] = Color.blue(color);

        return outLab;

    }

    private static double[] RGBToLAB(int r, int g, int b) {
        double[] outLab = new double[3];
        ColorUtils.RGBToLAB(r, g, b, outLab);
        return outLab;
    }

    private static int newRGB(int a, int r, int g, int b, double[] labdifference) {
        int color;
        double[] doubles = RGBToLAB(r, g, b);
        if (doubles.length == 3) {
            for (int i1 = 0; i1 < doubles.length; i1++) {
                doubles[i1] += labdifference[i1];
            }
            if (doubles[0] < 0) {
                doubles[0] = 0;
            } else if (doubles[0] > 100) {
                doubles[0] = 100;
            }
            if (doubles[1] > 127) {
                doubles[1] = 127;
            } else if (doubles[1] < -128) {
                doubles[1] = -128;
            }
            if (doubles[2] > 127) {
                doubles[2] = 127;
            } else if (doubles[2] < -128) {
                doubles[2] = -128;
            }
            double[] doubles2 = RGBToLAB(r, g, b);
            Log.e("www", "LAB(" + (int) doubles2[0] + "," + (int) doubles2[1] + "," + (int) doubles2[2] + ")" + "  ->  " + "LAB(" + (int) doubles[0] + "," + (int) doubles[1] + "," + (int) doubles[2] + ")");

        }
        int[] doubles1 = LABToRGB(doubles);
        //下面合成新的像素点
//        color = Color.argb(a, doubles1[0], doubles1[1], doubles1[2]);
        color = Color.rgb(doubles1[0], doubles1[1], doubles1[2]);//a,

        return color;
    }


    /**
     * 将图片转换为修改lab后的新图
     *
     * @param bitmap 原来图片
     * @return 新图片
     */
    public static Bitmap ImgaeToNegative(Bitmap bitmap, double[] labdifference, boolean isRefresh) {//double l, double a, double b
        //其实我们获得宽和高就是图片像素的宽和高
        //它们的乘积就是总共一张图片拥有的像素点数
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (isRefresh || bmp == null) {
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);// Bitmap.Config.RGB_565);
        }

//        SparseIntArray  oldPx = new SparseIntArray();

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
//        Log.e("xx", "像素点数:" + oldPx.length + ";width:" + width + ";height:" + height);

        for (int i = 0; i < width * height; i++) {

            color = oldPx[i];//获取一个原来的像素点
            r = Color.red(color);//获取红色分量，下同
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);
//            Log.v("ee", "像素点" + i + ":"  + r + "," + g + "," + b);//+ a + ","
            color = newRGB(a, r, g, b, labdifference);
            newPx[i] = color;
        }

        //然后重要的一步，为bmp设置新颜色了,该方法中的参数意义与getPixels中的一样
        //无非是将newPx写入到bmp中
        if (width <= bmp.getWidth()) {
            bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        }
        return bmp;
    }
}