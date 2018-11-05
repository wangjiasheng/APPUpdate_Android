package com.example.wjs.appupdate;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushService;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent pushintent=new Intent(context,PushService.class);//启动极光推送的服务
        context.startService(pushintent);

        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.i("wjs_wjs","JPush用户注册成功" + regId);
//            JPushInterface.setAlias(context, 1, regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.i("wjs_wjs","接受到推送下来的自定义消息");
            String title=bundle.getString(JPushInterface.EXTRA_TITLE);
            String message=bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

            try {
                JSONObject jsonObject=new JSONObject(extras);
                String download_url=jsonObject.getString("download_url");


                DownloadManager manager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(download_url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(title);
                request.setMimeType("application/vnd.android.package-archive");
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int modth=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int hour=calendar.get(Calendar.HOUR);
                int minute=calendar.get(Calendar.MINUTE);
                int second=calendar.get(Calendar.SECOND);
                int millisecond=calendar.get(Calendar.MILLISECOND);
                String fileNameInfo=year+"年"+modth+"月"+day+"日"+hour+"点"+minute+"分"+second+"秒"+millisecond+"_叮当医生.apk";
                File fileName=new File(Environment.getExternalStorageDirectory(),fileNameInfo);
                request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName.getAbsolutePath());
                long downloadId = manager.enqueue(request);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.i("wjs_wjs","接受到推送下来的通知");

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.i("wjs_wjs","用户点击打开了通知");

        } else {
            Log.i("wjs_wjs","Unhandled intent - " + intent.getAction());
        }
    }
}
