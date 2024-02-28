package com.whatscolors.demo.utils;


import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCvUtil {


    private static Bitmap bmp;


    /**
     * 将图片转换为修改lab后的新图
     *
     * @param bitmap 原来图片
     * @return 新图片
     */
    public static Bitmap ImgaeToNegative(Bitmap bitmap, double[] labdifference, boolean isRefresh) {//double l, double a, double b


        try {
//其实我们获得宽和高就是图片像素的宽和高
            //它们的乘积就是总共一张图片拥有的像素点数
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (isRefresh || bmp == null) {
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// Bitmap.Config.RGB_565);
            }
            Mat mat = new Mat(width, height, CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, mat);

            Mat bgrmat = new Mat();
            Mat dstmat = new Mat();

            Imgproc.cvtColor(mat, bgrmat, Imgproc.COLOR_BGRA2BGR);
            Imgproc.cvtColor(bgrmat, dstmat, Imgproc.COLOR_BGR2Lab);

            for (int i = 0; i < dstmat.rows(); i++) {
                for (int j = 0; j < dstmat.cols(); j++) {
                    double[] doubles = dstmat.get(i, j);
                    Log.e("www", "LAB(" + (int) doubles[0] + "," + (int) doubles[1] + "," + (int) doubles[2] + ")");

                    if (doubles != null && doubles.length == 3) {
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
                    }
                    Log.e("ee", "LAB(" + (int) doubles[0] + "," + (int) doubles[1] + "," + (int) doubles[2] + ")");
                    dstmat.put(i, j, doubles);

                }
            }
            Mat newbgrmat = new Mat();

            Imgproc.cvtColor(dstmat, newbgrmat, Imgproc.COLOR_Lab2BGR);
            Imgproc.cvtColor(newbgrmat, newbgrmat, Imgproc.COLOR_BGR2BGRA);

            Utils.matToBitmap(newbgrmat, bmp);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }




    public static Bitmap ImgaeToNegative4(Bitmap srcbmp, double[] labdifference) {//double l, double a, double b


        LogUtils.loge("调整lab值原图片大小：" + srcbmp.getByteCount());


        Mat bgrmat = null, dstmat = null, newbgrmat = null, mat = null, newdstmat = null;
        int width = srcbmp.getWidth();
        int height = srcbmp.getHeight();
        //
        Bitmap rstmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        try {
            //CV_8UC4--则可以创建----- 8位无符号的四通道---带透明色的RGB图像
            mat = new Mat(width, height, CvType.CV_8UC4);
            Utils.bitmapToMat(srcbmp, mat);
            bgrmat = new Mat();
            dstmat = new Mat();

            LogUtils.loge("开始COLOR_BGR2Lab : " + System.currentTimeMillis());

            Imgproc.cvtColor(mat, bgrmat, Imgproc.COLOR_BGRA2BGR);
            Imgproc.cvtColor(bgrmat, dstmat, Imgproc.COLOR_BGR2Lab);


            LogUtils.loge("dstmat.rows():" + dstmat.rows() + "dstmat.cols():" + dstmat.cols());

            LogUtils.loge("for循环改变lab值 : " + System.currentTimeMillis());

            for (int i = 0; i < dstmat.rows(); i++) {
                for (int j = 0; j < dstmat.cols(); j++) {
                    double[] doubles0 = dstmat.get(i, j);

                    if (doubles0 != null && doubles0.length == 3) {
                        double[] doubles = {doubles0[0] * 100 / 255, (doubles0[1] - 128), (doubles0[2] - 128)};
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
                        double[] doubles1 = {doubles[0] * 255 / 100, doubles[1] + 128, doubles[2] + 128};

//                        Log.v("we", "坐标(" + i + "," + j + "):" + "LAB(" + (int) doubles0[0] * 100 / 255 + "," + (int) (doubles0[1] - 128) + "," + (int) (doubles0[2] - 128) + ")" + "---------->" + "LAB(" + (int) doubles[0] + "," + (int) doubles[1] + "," + (int) doubles[2] + ")");
                        dstmat.put(i, j, doubles1);
                    }

                }
            }
            LogUtils.loge("结束for循环改变lab值 : " + System.currentTimeMillis());

            newbgrmat = new Mat();
//            newdstmat = new Mat();
            Imgproc.cvtColor(dstmat, newbgrmat, Imgproc.COLOR_Lab2BGR);
//            Imgproc.cvtColor(newbgrmat, newdstmat, Imgproc.COLOR_BGR2BGRA);

            Utils.matToBitmap(newbgrmat, rstmp);

            LogUtils.loge("结束对newbgrmat的赋值 : " + System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (newbgrmat != null) {
                newbgrmat.release();
            }
            if (bgrmat != null) {
                bgrmat.release();
            }
            if (dstmat != null) {
                dstmat.release();
            }
            if (mat != null) {
                mat.release();
            }
        }

        return rstmp;
    }

}