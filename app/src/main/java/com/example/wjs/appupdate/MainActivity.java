package com.example.wjs.appupdate;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.huawei.android.hms.ActivityTest;
import com.wjs.network.task.HttpTask;
import com.wjs.network.task.HttpTaskCallback;
import com.wjs.upatetask.NetworkTask;
import com.wjs.upatetask.URLBuilder;
import com.wjs.updatelib.UpdateDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends AppCompatActivity {
    public static void openStart(Context context){
            try {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                intent.setComponent(componentName);
            }catch (Exception ex){
                try {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    intent.setComponent(componentName);
                }catch (Exception e){
                    Intent intent = new Intent();
                    intent=new Intent(Settings.ACTION_SETTINGS);
                    context.startActivity(intent);
                }
            }
    }
    mDownBroadCast mDownloadBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForUpdate();

        /*Intent intent=new Intent(this,ActivityTest.class);
        startActivity(intent);*/


        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        intentFilter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        mDownloadBroadcast=new mDownBroadCast();
        registerReceiver(mDownloadBroadcast,intentFilter);

        Set<String> set=new HashSet<String>();
        //set.add("wangjiashheng");
        set.add("all");
        JPushInterface.setTags(this,10000,set);
    }
    public class mDownBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downId);

                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor c = manager.query(query);
                if(c.moveToFirst()) {
                    //获取文件下载路径
                    String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    //如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
                    if(filename != null){
                        File mFile=new File(filename);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri1 = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", mFile);
                            intent1.setDataAndType(uri1, "application/vnd.android.package-archive");
                        } else {
                            intent1.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
                        }
                        try {
                            startActivity(intent1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadBroadcast != null) {
            unregisterReceiver(mDownloadBroadcast);
        }
    }

    public void checkForUpdate(){
        new NetworkTask(this,new URLBuilder(this).builderHost("192.168.0.139").incremental_update(true).build()).execute();
    }
    public void getLocalApkInfo(){
        PackageManager manager=getPackageManager();
        PackageInfo packageInfos= null;
        try {
            packageInfos = manager.getPackageInfo("com.ddzybj.zydoctor", PackageManager.GET_CONFIGURATIONS);
            String versionName=packageInfos.versionName;
            int versionCode=packageInfos.versionCode;
            APKInfo info=new APKInfo();
            info.setDownSize(0);
            info.setMessage(null);
            info.setIncrementalupdate(false);
            info.setSimple(true);
            info.setVersionCode(versionCode);
            info.setVersionName(versionName);
            info.setAppIcon(null);
            info.setDownUrl(null);
            info.setApkName("叮当国医");
            // onSucess(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public Object onCreateBean(String requestResult) {
        try {
            JSONObject jsonObject=new JSONObject(requestResult);
            APKInfo apkInfo=new APKInfo();
            apkInfo.setApkName(jsonObject.getString("apkName"));
            apkInfo.setVersionName(jsonObject.getString("versionName"));
            apkInfo.setDownUrl(jsonObject.getString("downUrl"));
            apkInfo.setAppIcon(jsonObject.getString("appIcon"));
            apkInfo.setVersionCode(jsonObject.getInt("versionCode"));
            apkInfo.setSimple(jsonObject.getBoolean("streamline"));
            apkInfo.setIncrementalupdate(jsonObject.getBoolean("incrementalupdate"));
            if(!jsonObject.isNull("message")){
                JSONArray apkmessageobj= jsonObject.getJSONArray("message");
                List<String> apkmessageList=new ArrayList<String>();
                for(int i=0;i<apkmessageobj.length();i++){
                    apkmessageList.add(apkmessageobj.getString(i));
                }
                apkInfo.setMessage(apkmessageList);
            }
            apkInfo.setDownSize(jsonObject.getLong("downSize"));
            return apkInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void onSucess(APKInfo bean) {
        UpdateDialog updateDialog=new UpdateDialog();
        updateDialog.updateApp(MainActivity.this,bean.isIncrementalupdate(),bean.getVersionCode(),bean.getVersionName(),bean.getDownSize(),bean.getMessage(),bean.getDownUrl(),bean.isSimple());
    }

}
