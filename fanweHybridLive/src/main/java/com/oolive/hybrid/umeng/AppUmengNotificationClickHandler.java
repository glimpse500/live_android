package com.oolive.hybrid.umeng;

import android.content.Context;

import com.oolive.hybrid.app.App;
import com.oolive.hybrid.push.AuctionPayPushRunnable;
import com.oolive.hybrid.push.LivePushRunnable;
import com.oolive.hybrid.push.PushRunnable;
import com.oolive.hybrid.push.UrlPushRunnable;
import com.oolive.hybrid.push.msg.PushMsg;
import com.oolive.library.common.SDActivityManager;
import com.oolive.library.utils.SDPackageUtil;
import com.oolive.live.activity.LiveMainActivity;
import com.oolive.hybrid.constant.Constant;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-1-12 上午10:33:36 类说明 该Handler是在BroadcastReceiver中被调用，故
 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
 */
public class AppUmengNotificationClickHandler extends UmengNotificationClickHandler {
    @Override
    public void autoUpdate(Context arg0, UMessage arg1) {
        super.autoUpdate(arg0, arg1);
    }

    @Override
    public void dismissNotification(Context arg0, UMessage arg1) {
        super.dismissNotification(arg0, arg1);
    }

    @Override
    public void handleMessage(Context arg0, UMessage arg1) {
        super.handleMessage(arg0, arg1);
    }

    @Override
    public void openActivity(Context arg0, UMessage arg1) {
        super.openActivity(arg0, arg1);
    }

    @Override
    public void openUrl(Context arg0, UMessage arg1) {
        super.openUrl(arg0, arg1);
    }

    @Override
    public void launchApp(Context context, UMessage msg) {
        dealMsg(context, msg);
    }

    @Override
    public void dealWithCustomAction(Context context, UMessage msg) {
        dealMsg(context, msg);
        super.dealWithCustomAction(context, msg);
    }

    private void dealMsg(Context context, UMessage msg) {
        boolean launchApp = false;

        PushMsg pushMsg = new PushRunnable(context, msg).parseObject(PushMsg.class);
        if (pushMsg != null) {
            PushRunnable runnable = null;
            switch (pushMsg.getType()) {
                case Constant.PushType.NORMAL:

                    break;
                case Constant.PushType.LIVE:
                    runnable = new LivePushRunnable(context, msg);
                    break;
                case Constant.PushType.URL:
                    runnable = new UrlPushRunnable(context, msg);
                    return;
                case Constant.PushType.AUCTION_PAY:
                    runnable = new AuctionPayPushRunnable(context, msg);
                    break;
                default:
                    break;
            }
            if (runnable != null) {
                if (SDActivityManager.getInstance().isEmpty()) {
                    // app已经退出
                    runnable.setStartActivity(LiveMainActivity.class);
                    App.getApplication().setPushRunnable(runnable);
                    launchApp = true;
                } else {
                    runnable.run();
                }
            }
        }

        if (launchApp) {
            SDPackageUtil.startCurrentApp();
        }
    }

}
