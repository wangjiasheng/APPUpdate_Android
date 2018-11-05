package com.wjs.push;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

public class HWPushReceiver extends PushReceiver {
    /**
     * 连接上华为服务时会调用,可以获取token值
     *
     * @param context
     * @param token
     * @param extras
     */
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        String content = "get token and belongId successful, token = " + token + ",belongId = " + belongId;
        Log.e("wjs_wjs", content);
    }
    /**
     * 透传消息的回调方法
     *
     * @param context
     * @param msg
     * @param bundle
     * @return
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String message=new String(msg, "UTF-8");
            String content = "-------Receive a Push pass-by message： " + message;
            Log.e("wjs_wjs", content);

            try {
                JSONObject jsonObject=new JSONObject(message);
                String download_url=jsonObject.getString("download_url");


                DownloadManager manager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(download_url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("变体");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 自定义的消息的回调方法
     *
     * @param context
     * @param event
     * @param extras
     */
    @Override
    public void onEvent(Context context, PushReceiver.Event event, Bundle extras) {
        Log.e("wjs_wjs", "onEvent");
        switch (event){
            case NOTIFICATION_OPENED:
                break;
            case NOTIFICATION_CLICK_BTN:
                break;
        }
    }

    /**
     * 连接状态的回调方法
     *
     * @param context
     * @param pushState
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
            String content = "---------The current push status： " + (pushState ? "Connected" :
                    "Disconnected");
            Log.e("wjs_wjs", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
