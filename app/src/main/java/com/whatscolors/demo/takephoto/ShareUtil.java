package com.whatscolors.demo.takephoto;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;

public class ShareUtil {
    public static void shareFile(Context context, Uri uri) {
        if (context == null || uri == null) {
            return;
        }


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");

//// 如果需要指定分享到某个app，配置 componentName 即可
//            if (!TextUtils.isEmpty(componentName) && "com.tencent.mm".equals(componentName)) {
//                // 分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
//                ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
//                intent.setComponent(comp);
//            }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        // 授予目录临时共享权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        String fileType = "image/jpeg";
        intent.setType(fileType);
//        Uri fileUri = getFileUri(context, file);
//        if (fileUri != null && !TextUtils.isEmpty(fileUri.toString())) {
//            ContentResolver contentResolver = context.getContentResolver();
//            fileType = contentResolver.getType(fileUri);
//        }

//        if (TextUtils.isEmpty(fileType)) {
//            fileType = getFileType(filePath); // 使用上面的根据文件头信息获取文件类型的方法
//        }

//        if (TextUtils.isEmpty(fileType)) {
//            fileType = "*/*";
//        }

//        intent.setDataAndType(uri, fileType);

        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Uri getFileUri(Context context, File file) {
        Uri uri;
        // 低版本直接用 Uri.fromFile
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            //  使用 FileProvider 会在某些 app 下不支持（在使用FileProvider 方式情况下QQ不能支持图片、视频分享，微信不支持视频分享）
            uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider",
                    file);

            ContentResolver cR = context.getContentResolver();
            if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                String fileType = cR.getType(uri);
// 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                if (!TextUtils.isEmpty(fileType)) {
                    if (fileType.contains("video/")) {
                        uri = getVideoContentUri(context, file);
                    } else if (fileType.contains("image/")) {
                        uri = getImageContentUri(context, file);
                    } else if (fileType.contains("audio/")) {
                        uri = getAudioContentUri(context, file);
                    }
                }
            }
        }
        return uri;
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/audio/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 删除或增加图片、视频等媒体资源文件时 通知系统更新媒体库，重新扫描
     *
     * @param filePath 文件路径，包括后缀
     */
    public static void notifyScanMediaFile(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return;
        }
        MediaScannerConnection.scanFile(context,
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
}
