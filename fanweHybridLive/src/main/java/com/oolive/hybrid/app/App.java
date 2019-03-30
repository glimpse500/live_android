package com.oolive.hybrid.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import androidx.multidex.MultiDex;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.oolive.hybrid.activity.MainActivity;
import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.event.EExitApp;
import com.oolive.hybrid.event.EJsLogout;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.map.tencent.SDTencentMapManager;
import com.oolive.hybrid.push.PushRunnable;
import com.oolive.hybrid.utils.RetryInitWorker;
import com.fanwe.lib.cache.SDDisk;
import com.fanwe.lib.recorder.SDMediaRecorder;
import com.oolive.library.SDLibrary;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.common.SDActivityManager;
import com.oolive.library.common.SDHandlerManager;
import com.oolive.library.receiver.SDHeadsetPlugReceiver;
import com.oolive.library.receiver.SDNetworkReceiver;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDPackageUtil;
import com.oolive.live.DebugHelper;
import com.oolive.live.LiveConstant;
import com.oolive.live.LiveInitChat;
import com.oolive.live.activity.LiveLoginActivity;
import com.oolive.live.common.AppRuntimeWorker;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.common.JsonObjectConverter;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EOnCallStateChanged;
import com.oolive.live.event.EUserLoginSuccess;
import com.oolive.live.event.EUserLogout;
import com.oolive.live.model.App_userinfoActModel;
import com.oolive.live.utils.StorageFileUtils;
import com.squareup.leakcanary.LeakCanary;
import com.sunday.eventbus.SDEventManager;
import com.tencent.rtmp.ITXLiveBaseListener;
import com.tencent.rtmp.TXLiveConstants;
import com.umeng.analytics.MobclickAgent;

import org.xutils.x;

import de.greenrobot.event.SubscriberExceptionEvent;

public class App extends Application implements ITXLiveBaseListener {
    private static App instance;
    private PushRunnable pushRunnable;

    public static App getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        if (SDPackageUtil.isMainProcess(this)) {
            // 主进程
            SDLibrary.getInstance().init(this);

            SDDisk.init(this);
            SDDisk.setGlobalObjectConverter(new JsonObjectConverter());
            SDDisk.setDebug(ApkConstant.DEBUG);

            LeakCanary.install(this);
            MobclickAgent.setCatchUncaughtExceptions(false);
            //UMShareAPI.get(this);
            SDEventManager.register(this);
            SDNetworkReceiver.registerReceiver(this);
            SDHeadsetPlugReceiver.registerReceiver(this);
            x.Ext.init(this);
            SDTencentMapManager.getInstance().init(this);
            new LiveInitChat().init(this);
            initSystemListener();
            SDMediaRecorder.getInstance().init(this);
            LogUtil.isDebug = ApkConstant.DEBUG;
            DebugHelper.init(this);


            if (ApkConstant.DEBUG) {
                //直播sdk日志
//                TXLiveBase.getInstance().setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);
 //               TXLiveBase.getInstance().listener = this;
//                LogUtil.i("Tencent Live SDK Version:" + TXLiveBase.getSDKVersionStr());
            }

            //聚宝付初始化
            //FWPay.init(this, getResources().getString(R.string.app_id), true);
        }
        // 友盟推送需要在每个进程初始化
        //UmengPushManager.init(this);
    }

    private void initSystemListener() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                EOnCallStateChanged event = new EOnCallStateChanged();
                event.state = state;
                event.incomingNumber = incomingNumber;
                SDEventManager.post(event);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public boolean isPushStartActivity(Class<?> clazz) {
        boolean result = false;
        if (pushRunnable != null) {
            result = pushRunnable.getStartActivity() == clazz;
        }
        return result;
    }

    public void setPushRunnable(PushRunnable pushRunnable) {
        this.pushRunnable = pushRunnable;
    }

    public PushRunnable getPushRunnable() {
        return pushRunnable;
    }

    public void startPushRunnable() {
        if (pushRunnable != null) {
            pushRunnable.run();
            pushRunnable = null;
        }
    }


    public void exitApp(boolean isBackground) {
        AppRuntimeWorker.logout();
        SDActivityManager.getInstance().finishAllActivity();
        EExitApp event = new EExitApp();
        SDEventManager.post(event);
        if (!isBackground) {
            System.exit(0);
        }
    }

    /**
     * 退出登录
     *
     * @param post
     */
    public void logout(boolean post) {
        logout(post, true, false);
    }

    public void logout(boolean post, boolean isStartLogin, boolean isStartH5Main) {
        UserModelDao.delete();
        AppRuntimeWorker.setUsersig(null);
        AppRuntimeWorker.logout();
        CommonInterface.requestLogout(null);
        RetryInitWorker.getInstance().start();
        StorageFileUtils.deleteCrop_imageFile();

        if (isStartLogin) {
            Intent intent = new Intent(this, LiveLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SDActivityManager.getInstance().getLastActivity().startActivity(intent);
        } else if (isStartH5Main) {
            //否则启动H5页面
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SDActivityManager.getInstance().getLastActivity().startActivity(intent);
        }

        if (post) {
            EUserLogout event = new EUserLogout();
            SDEventManager.post(event);
        }
    }

    /**
     * 退出登录
     *
     * @param event
     */
    public void onEventMainThread(EJsLogout event) {
        logout(true);
    }

    public void onEventMainThread(EUserLoginSuccess event) {
        AppRuntimeWorker.setUsersig(null);
        CommonInterface.requestMyUserInfo(new AppRequestCallback<App_userinfoActModel>() {
            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.getStatus() == 1) {
                    CommonInterface.requestUsersig(null);
                }
            }
        });
    }

    public void onEventMainThread(SubscriberExceptionEvent event) {
        LogUtil.e("onEventMainThread:" + event.throwable.toString());
    }

    @Override
    public void onTerminate() {
        SDEventManager.unregister(instance);
        SDNetworkReceiver.unregisterReceiver(this);
        SDHandlerManager.stopBackgroundHandler();
        SDMediaRecorder.getInstance().release();
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void OnLog(int level, String module, String log) {
        switch (level) {
            case TXLiveConstants.LOG_LEVEL_ERROR:
                Log.e(LiveConstant.LiveSdkTag.TAG_SDK_TENCENT, module + "----------" + log);
                break;
            case TXLiveConstants.LOG_LEVEL_WARN:
                Log.w(LiveConstant.LiveSdkTag.TAG_SDK_TENCENT, module + "----------" + log);
                break;
            case TXLiveConstants.LOG_LEVEL_INFO:
                Log.i(LiveConstant.LiveSdkTag.TAG_SDK_TENCENT, module + "----------" + log);
                break;
            case TXLiveConstants.LOG_LEVEL_DEBUG:
                Log.d(LiveConstant.LiveSdkTag.TAG_SDK_TENCENT, module + "----------" + log);
                break;
            default:
                Log.d(LiveConstant.LiveSdkTag.TAG_SDK_TENCENT, module + "----------" + log);
        }
    }
}
