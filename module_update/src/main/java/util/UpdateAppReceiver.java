package util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.File;


/**
 * Created by Teprinciple on 2017/11/3.
 */

 public class UpdateAppReceiver extends BroadcastReceiver {


    private Uri contentUri;

    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyId = 1;
        int progress = intent.getIntExtra("progress", 0);
        String title = intent.getStringExtra("title");

        NotificationManager nm = null;
        if (UpdateAppUtils.showNotification){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(title);//"正在下载 "+
            builder.setSmallIcon(android.R.mipmap.sym_def_app_icon);
            builder.setProgress(100,progress,false);
            Notification notification = builder.build();
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notifyId,notification);
        }


        if (progress == 100){
            if (nm!=null)nm.cancel(notifyId);

            if (DownloadAppUtils.downloadUpdateApkFilePath != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                File apkFile = new File(DownloadAppUtils.downloadUpdateApkFilePath);
                if ( UpdateAppUtils.needFitAndroidN &&  Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    contentUri = FileProvider.getUriForFile(
                            context, context.getPackageName() + ".fileprovider", apkFile);
                    i.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    contentUri=Uri.fromFile(apkFile);
                }
                i.setDataAndType(contentUri,
                        "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
