package com.whatscolors.demo.service;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.whatscolors.demo.utils.DownLoadUtils;

import static com.whatscolors.demo.MyConfig.GIF_VERSION;
import static com.whatscolors.demo.MyConfig.gifname;
import static com.whatscolors.demo.utils.Utils.getFilePath;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Author:      wangwei
 * Date:        2020/11/11 10:35
 * Version:
 */
public class UpdateGifService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateGifService() {
        super("UpdateGifService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            String url = intent.getStringExtra("gifurl");//SPUtils.getSharedStringData(BaseApplication.getAppContext(), "gifurl");
            int gifversion = intent.getIntExtra("gifversion", GIF_VERSION);
            DownLoadUtils.downloadFile(url, getFilePath(), gifname, gifversion);
        }
    }

}
