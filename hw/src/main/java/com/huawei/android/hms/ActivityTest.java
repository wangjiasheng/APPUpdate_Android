package com.huawei.android.hms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;

import static com.huawei.hms.activity.BridgeActivity.EXTRA_RESULT;

public class ActivityTest extends Activity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {
    private HuaweiApiClient client;
    private boolean mResolvingError=false;
    private int REQUEST_RESOLVE_ERROR=1000;
    private String huaweikoken="0866342030745516300002473600CN01";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new HuaweiApiClient.Builder(this)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect(this);
    }
    private void getTokenAsyn() {
        if(!client.isConnected()) {
            Log.i("wjs_wjs", "获取token失败，原因：HuaweiApiClient未连接");
            client.connect(this);
            return;
        }
        Log.i("wjs_wjs", "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override

            public void onResult(TokenResult result) {

            }
        });
    }
    @Override
    public void onConnected() {
        Log.i("wjs_wjs","onConnected");
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("wjs_wjs","onConnectionSuspended");
        if (!this.isDestroyed() && !this.isFinishing()) {
            client.connect(this);
        }
        Log.i("wjs_wjs", "HuaweiApiClient 连接断开");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("wjs_wjs","onConnectionFailed:"+connectionResult.getErrorCode());
        if(HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {
            final int errorCode = connectionResult.getErrorCode();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用, xxxxxx.this 为当前界面的activity
                    HuaweiApiAvailability.getInstance().resolveError(ActivityTest.this, errorCode, REQUEST_RESOLVE_ERROR);
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_RESOLVE_ERROR) {
            if(resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(EXTRA_RESULT, 0);
                if(result == ConnectionResult.SUCCESS) {
                    Log.i("wjs_wjs", "错误成功解决");
                    if (!client.isConnecting() && !client.isConnected()) {
                        client.connect(ActivityTest.this);
                    }
                } else if(result == ConnectionResult.CANCELED) {
                    Log.i("wjs_wjs", "解决错误过程被用户取消");
                } else if(result == ConnectionResult.INTERNAL_ERROR) {
                    Log.i("wjs_wjs", "发生内部错误，重试可以解决");
                    //开发者可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    Log.i("wjs_wjs", "未知返回码");
                }
            } else {
                Log.i("wjs_wjs", "调用解决方案发生错误");
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }
}
