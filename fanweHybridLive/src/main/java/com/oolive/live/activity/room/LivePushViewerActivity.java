package com.oolive.live.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.oolive.hybrid.event.EUnLogin;
import com.fanwe.lib.dialog.ISDDialogConfirm;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.oolive.library.model.SDDelayRunnable;
import com.oolive.library.receiver.SDNetworkReceiver;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.R;
import com.oolive.live.appview.LiveLinkMicGroupView;
import com.oolive.live.appview.LiveLinkMicView;
import com.oolive.live.appview.LiveVideoView;
import com.oolive.live.control.LivePlayerSDK;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.dialog.common.AppDialogConfirm;
import com.oolive.live.event.EImOnForceOffline;
import com.oolive.live.event.EOnBackground;
import com.oolive.live.event.EOnCallStateChanged;
import com.oolive.live.event.EOnResumeFromBackground;
import com.oolive.live.event.EPushViewerOnDestroy;
import com.oolive.live.model.App_get_videoActModel;
import com.oolive.live.model.LiveQualityData;
import com.oolive.live.model.custommsg.CustomMsgEndVideo;
import com.oolive.live.model.custommsg.CustomMsgStopLinkMic;
import com.oolive.live.model.custommsg.CustomMsgStopLive;
import com.oolive.live.model.custommsg.data.DataLinkMicInfoModel;
import com.sunday.eventbus.SDEventManager;
import com.tencent.rtmp.TXLivePlayer;

/**
 * 推流直播间观众界面
 */
public class LivePushViewerActivity extends LiveLayoutViewerExtendActivity implements LivePlayerSDK.TPlayCallback {
    private LiveVideoView mPlayView;
    private LiveLinkMicGroupView mLinkMicGroupView;
    /**
     * 是否正在播放主播的加速拉流地址
     */
    private boolean mIsPlayACC = false;
    private String mAccUrl;

    @Override
    protected int onCreateContentView() {
        return R.layout.act_live_push_viewer;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        initPlayer();
        initLinkMicGroupView();
        requestRoomInfo();
        if (validateParams(getRoomId(), getGroupId(), getCreaterId())) {
            requestRoomInfo();
        }
        //To do , to be removed
        hideLoadingVideo();
    }

    /**
     * 初始化连麦view
     */
    private void initLinkMicGroupView() {
        mLinkMicGroupView = find(R.id.view_link_mic_group);
        mLinkMicGroupView.mCallBack.set(new LiveLinkMicGroupView.LiveLinkMicGroupViewCallback() {
            @Override
            public void onPlayDisconnect(String userId) {
            }

            @Override
            public void onPlayRecvFirstFrame(String userId) {
            }

            @Override
            public void onClickView(LiveLinkMicView view) {
            }

            @Override
            public void onPushStart(LiveLinkMicView view) {
                if (!TextUtils.isEmpty(mAccUrl)) {
                    if (!mIsPlayACC) {
                        getViewerBusiness().requestMixStream(UserModelDao.getUserId());
                        getPlayer().stopPlay();
                        getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                        getPlayer().setUrl(mAccUrl);
                        getPlayer().startPlay();
                        mIsPlayACC = true;
                        LogUtil.i("play acc:" + mAccUrl);
                    }
                } else {
                    LogUtil.e("大主播acc流地址为空");
                }
            }
        });
    }

    /**
     * 初始化拉流对象
     */
    private void initPlayer() {
        mPlayView = find(R.id.view_video);
        mPlayView.setPlayCallback(this);
    }

    public LivePlayerSDK getPlayer() {
        return mPlayView.getPlayer();
    }

    @Override
    public LiveQualityData onBsGetLiveQualityData() {
        return getPlayer().getLiveQualityData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            int oldRoomId = getRoomId();
            int newRoomId = intent.getIntExtra(EXTRA_ROOM_ID, 0);
            if (newRoomId != oldRoomId) {
                setIntent(intent);
                getViewerBusiness().exitRoom(false);
                init(null);
            } else {
                SDToast.showToast("已经在直播间中");
            }
        }
        super.onNewIntent(intent);
    }

    protected boolean validateParams(int roomId, String groupId, String createrId) {
        if (roomId <= 0) {
            SDToast.showToast("房间id为空");
            finish();
            return false;
        }

        if (TextUtils.isEmpty(groupId)) {
            SDToast.showToast("聊天室id为空");
            finish();
            return false;
        }

        if (TextUtils.isEmpty(createrId)) {
            SDToast.showToast("主播id为空");
            finish();
            return false;
        }

        return true;
    }

    @Override
    protected void initIM() {
        super.initIM();
        /*
        final String groupId = getGroupId();
        getViewerIM().joinGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                onErrorJoinGroup(code, desc);
            }

            @Override
            public void onSuccess() {
                onSuccessJoinGroup(groupId);
            }
        });*/
    }

    /**
     * 加入聊天组失败回调
     *
     * @param code
     * @param desc
     */
    public void onErrorJoinGroup(int code, String desc) {
        AppDialogConfirm dialog = new AppDialogConfirm(this);
        dialog.setTextContent("加入聊天组失败:" + code + "，是否重试").setTextCancel("退出").setTextConfirm("重试");
        dialog.setCancelable(false);
        dialog.setCallback(new ISDDialogConfirm.Callback() {
            @Override
            public void onClickCancel(View v, SDDialogBase dialog) {
                getViewerBusiness().exitRoom(true);
            }

            @Override
            public void onClickConfirm(View v, SDDialogBase dialog) {
                initIM();
            }
        }).show();
    }

    @Override
    protected void onSuccessJoinGroup(String groupId) {
        super.onSuccessJoinGroup(groupId);
        sendViewerJoinMsg();
    }

    @Override
    public void onMsgDataLinkMicInfo(DataLinkMicInfoModel model) {
        super.onMsgDataLinkMicInfo(model);

        boolean isLocalUserLinkMic = model.isLocalUserLinkMic();
        if (isLocalUserLinkMic) {
            if (getViewerBusiness().isInLinkMic()) {
                mAccUrl = model.getPlay_rtmp_acc();
                mLinkMicGroupView.setLinkMicInfo(model);
            }
        } else {
            stopLinkMic(true, false);
        }
    }

    @Override
    public void onMsgStopLinkMic(CustomMsgStopLinkMic msg) {
        super.onMsgStopLinkMic(msg);
        stopLinkMic(true, false);
        SDToast.showToast("主播关闭了连麦");
    }

    @Override
    protected void onClickStopLinkMic() {
        super.onClickStopLinkMic();
        stopLinkMic(true, true);
    }

    /**
     * 暂停连麦
     */
    private void pauseLinkMic() {
        if (getViewerBusiness().isInLinkMic()) {
            mLinkMicGroupView.onPause();
        }
    }

    /**
     * 恢复连麦
     */
    private void resumeLinkMic() {
        if (getViewerBusiness().isInLinkMic()) {
            mLinkMicGroupView.onResume();
        }
    }

    /**
     * 停止连麦
     *
     * @param needPlayOriginal 停止连麦后是否需要拉连麦之前的主播视频流
     * @param sendStopMsg      是否发送结束连麦的消息
     */
    private void stopLinkMic(boolean needPlayOriginal, boolean sendStopMsg) {
        if (getViewerBusiness().isInLinkMic()) {
            getViewerBusiness().stopLinkMic(sendStopMsg);

            mLinkMicGroupView.resetAllView();
            if (needPlayOriginal) {
                if (mIsPlayACC) {
                    playUrlByRoomInfo();
                    mIsPlayACC = false;
                }
            }
        }
    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg) {
        super.onMsgEndVideo(msg);
        getViewerBusiness().exitRoom(false);
    }

    @Override
    public void onMsgStopLive(CustomMsgStopLive msg) {
        super.onMsgStopLive(msg);
        SDToast.showToast(msg.getDesc());
        getViewerBusiness().exitRoom(true);
    }

    @Override
    public void onBsViewerExitRoom(boolean needFinishActivity) {
        super.onBsViewerExitRoom(needFinishActivity);
        mIsPlayACC = false;
        mAccUrl = null;

        destroyIM();
        stopLinkMic(false, true);
        getPlayer().stopPlay();
        if (mIsNeedShowFinish) {
            addLiveFinish();
            return;
        }

        if (needFinishActivity) {
            finish();
        }
    }

    @Override
    protected void destroyIM() {
        super.destroyIM();
        getViewerIM().destroyIM();
    }

    @Override
    public void onBsRequestRoomInfoSuccess(App_get_videoActModel actModel) {
        int rId = actModel.getRoom_id();
        String gId = actModel.getGroup_id();
        String cId = actModel.getUser_id();


        if (!validateParams(rId, gId, cId)) {
            SDToast.showToast("Illeagl RoomID | Group ID | UserID");
            /*to do
                return;
                    */
        }

        super.onBsRequestRoomInfoSuccess(actModel);
        LogUtil.i("switchVideoViewMode");
        switchVideoViewMode();
        LogUtil.i("startJoinRoom");
        getViewerBusiness().startJoinRoom();
    }

    private void switchVideoViewMode() {
        if (mPlayView == null) {
            LogUtil.i("no play view");
            SDToast.showToast("No PlayView");
            /*to do
            return;
            */
        }
        if (getLiveBusiness().isPCCreate()) {
            getPlayer().setRenderModeAdjustResolution();
        } else {
            getPlayer().setRenderModeFill();
        }
    }

    @Override
    public void onBsRequestRoomInfoError(App_get_videoActModel actModel) {
        if (!actModel.canJoinRoom()) {
            super.onBsRequestRoomInfoError(actModel);
            return;
        }
        super.onBsRequestRoomInfoError(actModel);
        if (actModel.isVideoStoped()) {
            addLiveFinish();
        } else {
            getViewerBusiness().exitRoom(true);
        }
    }

    @Override
    public void onBsRequestRoomInfoException(String msg) {
        super.onBsRequestRoomInfoException(msg);

        AppDialogConfirm dialog = new AppDialogConfirm(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTextContent("请求直播间信息失败")
                .setTextCancel("退出").setTextConfirm("重试")
                .setCallback(new ISDDialogConfirm.Callback() {
                    @Override
                    public void onClickCancel(View v, SDDialogBase dialog) {
                        getViewerBusiness().exitRoom(true);
                    }

                    @Override
                    public void onClickConfirm(View v, SDDialogBase dialog) {
                        requestRoomInfo();
                    }
                }).show();
    }

    @Override
    public void onBsViewerStartJoinRoom() {
        super.onBsViewerStartJoinRoom();
        if (getRoomInfo() == null) {
            return;
        }
        String playUrl = getRoomInfo().getPlay_url();
        if (TextUtils.isEmpty(playUrl)) {
            requestRoomInfo();
        } else {
            startJoinRoomRunnable();
        }
    }

    private void startJoinRoomRunnable() {
        mJoinRoomRunnable.run();
    }

    /**
     * 加入房间runnable
     */
    private SDDelayRunnable mJoinRoomRunnable = new SDDelayRunnable() {

        @Override
        public void run() {
            initIM();
            playUrlByRoomInfo();
        }
    };

    /**
     * 根据接口返回的拉流地址开始拉流
     */
    protected void playUrlByRoomInfo() {
        if (getRoomInfo() == null) {
            return;
        }

        String url = getRoomInfo().getPlay_url();
        LogUtil.i("playUrlByRoomInfo:" + url);
        getPlayer().stopPlay();
        getPlayer().setUrl(url);
        getPlayer().startPlay();
        if (validatePlayUrl(url)) {
            getPlayer().stopPlay();
            getPlayer().setUrl(url);
            getPlayer().startPlay();
            LogUtil.i("play normal:" + url);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {

    }

    @Override
    public void onPlayBegin() {

    }

    @Override
    public void onPlayRecvFirstFrame() {
        hideLoadingVideo();
    }

    @Override
    public void onPlayProgress(long total, long progress) {

    }

    @Override
    public void onPlayEnd() {

    }

    @Override
    public void onPlayLoading() {

    }

    protected boolean validatePlayUrl(String playUrl) {
        if (TextUtils.isEmpty(playUrl)) {
            SDToast.showToast("未找到直播地址");
            return false;
        }

        if (playUrl.startsWith("rtmp://")) {
            getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
            getPlayer().setPlayType(TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        } else {
            SDToast.showToast("播放地址不合法");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @Override
    protected void onClickCloseRoom(View v) {
        getViewerBusiness().exitRoom(true);
    }

    private void showExitDialog() {
        AppDialogConfirm dialog = new AppDialogConfirm(this);
        dialog.setTextContent("确定要退出吗？");
        dialog.setCallback(new ISDDialogConfirm.Callback() {
            @Override
            public void onClickCancel(View v, SDDialogBase dialog) {

            }

            @Override
            public void onClickConfirm(View v, SDDialogBase dialog) {
                getViewerBusiness().exitRoom(true);
            }
        }).show();
    }


    public void onEventMainThread(EUnLogin event) {
        getViewerBusiness().exitRoom(true);
    }

    public void onEventMainThread(EImOnForceOffline event) {
        getViewerBusiness().exitRoom(true);
    }

    public void onEventMainThread(EOnCallStateChanged event) {
        switch (event.state) {
            case TelephonyManager.CALL_STATE_RINGING:
                sdkEnableAudio(false);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                sdkEnableAudio(false);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                sdkEnableAudio(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkChanged(SDNetworkReceiver.NetworkType type) {
        if (type == SDNetworkReceiver.NetworkType.Mobile) {
            AppDialogConfirm dialog = new AppDialogConfirm(this);
            dialog.setTextContent("当前处于数据网络下，会耗费较多流量，是否继续？").setTextCancel("否").setTextConfirm("是")
                    .setCallback(new ISDDialogConfirm.Callback() {
                        @Override
                        public void onClickCancel(View v, SDDialogBase dialog) {
                            getViewerBusiness().exitRoom(true);
                        }

                        @Override
                        public void onClickConfirm(View v, SDDialogBase dialog) {

                        }
                    }).show();
        }
        super.onNetworkChanged(type);
    }

    @Override
    protected void sdkEnableAudio(boolean enable) {
        getPlayer().setMute(!enable);
    }

    @Override
    protected void sdkPauseVideo() {
        super.sdkPauseVideo();
        getPlayer().stopPlay();
    }

    @Override
    protected void sdkResumeVideo() {
        super.sdkResumeVideo();
        getPlayer().startPlay();
    }

    @Override
    protected void sdkStopLinkMic() {
        super.sdkStopLinkMic();
        stopLinkMic(false, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseLinkMic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeLinkMic();
    }

    public void onEventMainThread(EOnBackground event) {
        getPlayer().stopPlay();
    }

    public void onEventMainThread(EOnResumeFromBackground event) {
        getPlayer().startPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJoinRoomRunnable.removeDelay();
        getPlayer().onDestroy();
        mLinkMicGroupView.onDestroy();
        SDEventManager.post(new EPushViewerOnDestroy());
    }
}
