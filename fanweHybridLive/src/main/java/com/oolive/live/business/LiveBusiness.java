package com.oolive.live.business;

import android.app.Activity;
import android.content.Context;

import com.oolive.games.model.custommsg.CustomMsgGameBanker;
import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.umeng.UmengSocialManager;
import com.fanwe.lib.looper.ISDLooper;
import com.fanwe.lib.looper.impl.SDSimpleLooper;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.listener.SDIterateCallback;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDCollectionUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.IMHelper;
import com.oolive.live.LiveInformation;
import com.oolive.live.R;
import com.oolive.live.activity.room.ILiveActivity;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.App_get_videoActModel;
import com.oolive.live.model.App_shareActModel;
import com.oolive.live.model.App_viewerActModel;
import com.oolive.live.model.LiveQualityData;
import com.oolive.live.model.RoomShareModel;
import com.oolive.live.model.RoomUserModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.custommsg.CustomMsgAcceptLinkMic;
import com.oolive.live.model.custommsg.CustomMsgApplyLinkMic;
import com.oolive.live.model.custommsg.CustomMsgCreaterComeback;
import com.oolive.live.model.custommsg.CustomMsgCreaterLeave;
import com.oolive.live.model.custommsg.CustomMsgData;
import com.oolive.live.model.custommsg.CustomMsgEndVideo;
import com.oolive.live.model.custommsg.CustomMsgGift;
import com.oolive.live.model.custommsg.CustomMsgLargeGift;
import com.oolive.live.model.custommsg.CustomMsgLight;
import com.oolive.live.model.custommsg.CustomMsgLiveMsg;
import com.oolive.live.model.custommsg.CustomMsgPopMsg;
import com.oolive.live.model.custommsg.CustomMsgRedEnvelope;
import com.oolive.live.model.custommsg.CustomMsgRejectLinkMic;
import com.oolive.live.model.custommsg.CustomMsgStopLinkMic;
import com.oolive.live.model.custommsg.CustomMsgStopLive;
import com.oolive.live.model.custommsg.CustomMsgViewerJoin;
import com.oolive.live.model.custommsg.CustomMsgViewerQuit;
import com.oolive.live.model.custommsg.CustomMsgWarning;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.live.model.custommsg.data.DataLinkMicInfoModel;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 直播间公共业务类
 */
public class LiveBusiness extends LiveBaseBusiness implements LiveMsgBusiness.LiveMsgBusinessCallback {
    private LiveMsgBusiness mMsgBusiness;
    private LiveBusinessCallback mBusinessCallback;
    private List<UserModel> mListViewer = new ArrayList<>();
    private ISDLooper mLiveQualityLooper;
    /**
     * 观众数量
     */
    private int mViewerNumber;
    /**
     * 印票数量
     */
    private long mTicket;
    /**
     * 最后一次刷新观众列表的时间
     */
    private long mLastViewerListTime;
    /**
     * 是否正在连麦
     */
    private boolean mIsInLinkMic = false;

    public LiveBusiness(ILiveActivity liveActivity) {
        super(liveActivity);
    }

    //----------setter getter start----------

    public boolean isInLinkMic() {
        return mIsInLinkMic;
    }

    public void setInLinkMic(boolean inLinkMic) {
        mIsInLinkMic = inLinkMic;
    }

    public void setBusinessCallback(LiveBusinessCallback businessCallback) {
        this.mBusinessCallback = businessCallback;
    }

    //----------setter getter end----------

    public boolean isPCCreate() {
        if (getLiveActivity().getRoomInfo() != null) {
            return getLiveActivity().getRoomInfo().getCreate_type() == 1;
        }
        return false;
    }

    public LiveQualityData getLiveQualityData() {
        return mBusinessCallback.onBsGetLiveQualityData();
    }

    /**
     * 设置观众数量
     *
     * @param number
     */
    public void setViewerNumber(int number) {
        this.mViewerNumber = number;

        if (this.mViewerNumber < 0) {
            this.mViewerNumber = 0;
        }
        notifyViewerNumberChange();
    }

    /**
     * 获得主播印票
     *
     * @return
     */
    public long getTicket() {
        return mTicket;
    }

    /**
     * 设置印票
     *
     * @param ticket
     */
    public void setTicket(long ticket) {
        if (ticket >= this.mTicket) {
            this.mTicket = ticket;
        }

        if (this.mTicket < 0) {
            this.mTicket = 0;
        }
        notifyTicketChange();
    }

    /**
     * 清空印票
     */
    public void clearTicket() {
        this.mTicket = 0;
        notifyTicketChange();
    }

    /**
     * 通知观众数量发生变化
     */
    private void notifyViewerNumberChange() {
        mBusinessCallback.onBsViewerNumberChange(mViewerNumber);
    }

    /**
     * 通知印票发生变化
     */
    private void notifyTicketChange() {
        mBusinessCallback.onBsTicketChange(mTicket);
    }

    public LiveMsgBusiness getMsgBusiness() {
        if (mMsgBusiness == null) {
            mMsgBusiness = new LiveMsgBusiness();
            mMsgBusiness.setBusinessCallback(this);
        }
        return mMsgBusiness;
    }


    /**
     * 发送分享成功消息
     */
    public void sendShareSuccessMsg() {
        UserModel userModel = UserModelDao.query();
        if (userModel == null) {
            return;
        }

        final String groupId = getLiveActivity().getGroupId();
        final CustomMsgLiveMsg msg = new CustomMsgLiveMsg();
        msg.setDesc(userModel.getNick_name() + " 分享了主播");
        IMHelper.sendMsgGroup(groupId, msg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                IMHelper.postMsgLocal(msg, groupId);
            }
        });
    }

    /**
     * 请求房间信息
     *
     * @param private_key 私密直播的私密key
     */
    public void requestRoomInfo(String private_key) {
        int isVod = getLiveActivity().isPlayback() ? 1 : 0;

        CommonInterface.requestRoomInfo(getLiveActivity().getRoomId(), isVod, private_key, new AppRequestCallback<App_get_videoActModel>() {
            @Override
            public String getCancelTag() {
                return getHttpCancelTag();
            }

            @Override
            protected void onSuccess(SDResponse sdResponse) {
                LogUtil.i("requestRoomInfo onSuccess");
                LogUtil.i(sdResponse.getResult());
                LiveInformation.getInstance().setRoomInfo(actModel);
                LogUtil.i("actModel :"+ actModel.isOk());
                if (actModel.isOk()) {

                    onRequestRoomInfoSuccess(actModel);
                } else {
                    LogUtil.i("requestRoomInfoError");
                    onRequestRoomInfoError(actModel);
                }
            }

            @Override
            protected void onError(SDResponse resp) {
                LogUtil.i("requestRoomInfo onError");
                LogUtil.i(resp.getResult());
                super.onError(resp);

                String msg = "request error";
                if (resp.getThrowable() != null) {
                    msg = resp.getThrowable().toString();
                }
                onRequestRoomInfoException(msg);
            }
        });
    }

    /**
     * 请求房间信息成功
     */
    protected void onRequestRoomInfoSuccess(App_get_videoActModel actModel) {
        LiveInformation.getInstance().setGroupId(actModel.getGroup_id());
        LiveInformation.getInstance().setCreaterId(actModel.getUser_id());
        LiveInformation.getInstance().setPrivate(actModel.getIs_private() == 1);
        LiveInformation.getInstance().setSdkType(actModel.getSdk_type());

        // 绑定主播数据
        RoomUserModel roomUserModel = actModel.getPodcast();
        if (roomUserModel != null) {
            UserModel userModel = roomUserModel.getUser();
            if (userModel != null) {
                mBusinessCallback.onBsBindCreaterData(userModel);
                clearTicket();
                setTicket(userModel.getTicket());
            }
        }
        //To Do
        LogUtil.i("onRequestRoomInfoSuccess : " + actModel.getSdk_type());
        bindShowOperateViewer(actModel);

        // 绑定观众数量
        setViewerNumber(actModel.getViewer_num());
        onRequestViewerListSuccess(actModel.getViewer());
        mBusinessCallback.onBsRequestRoomInfoSuccess(actModel);
    }

    /**
     * 绑定显示隐藏私密直播邀请观众的布局
     *
     * @param actModel
     */
    private void bindShowOperateViewer(App_get_videoActModel actModel) {
        if (getLiveActivity().isCreater()) {
            mBusinessCallback.onBsShowOperateViewer(getLiveActivity().isPrivate());
        } else {
            if (getLiveActivity().isPrivate()) {
                RoomUserModel roomUserModel = actModel.getPodcast();
                if (roomUserModel != null) {
                    mBusinessCallback.onBsShowOperateViewer(roomUserModel.getHas_admin() == 1);
                }
            } else {
                mBusinessCallback.onBsShowOperateViewer(false);
            }
        }
    }

    /**
     * 请求房间信息成功，但是业务失败
     */
    protected void onRequestRoomInfoError(App_get_videoActModel actModel) {
        mBusinessCallback.onBsRequestRoomInfoError(actModel);
    }

    protected void onRequestRoomInfoException(String msg) {
        mBusinessCallback.onBsRequestRoomInfoException(msg);
    }

    /**
     * 请求观众列表成功回调
     *
     * @param actModel
     */
    public void onRequestViewerListSuccess(App_viewerActModel actModel) {
        LogUtil.i("onRequestViewerListSuccess : ");
        if (actModel != null) {
            setViewerNumber(actModel.getWatch_number());
            setViewerList(actModel.getList());
        }
    }

    public void setViewerList(List<UserModel> list) {
        mListViewer.clear();
        if (list != null) {
            mListViewer.addAll(list);
        }

        removeCreaterIfNeed(mListViewer);
        mBusinessCallback.onBsRefreshViewerList(mListViewer);
    }

    private void removeCreaterIfNeed(List<UserModel> listModel) {
        if (getLiveActivity().getRoomInfo() != null && getLiveActivity().getRoomInfo().getLive_in() == 1) {
            SDCollectionUtil.iterate(listModel, new SDIterateCallback<UserModel>() {
                @Override
                public boolean next(int i, UserModel item, Iterator<UserModel> it) {
                    if (item.getUser_id().equals(getLiveActivity().getCreaterId())) {
                        it.remove();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 开始显示直播质量
     */
    public void startLiveQualityLooper(Context context) {
        boolean show = context.getResources().getBoolean(R.bool.show_live_sdk_info);
        if (!show) {
            return;
        }
        if (mLiveQualityLooper == null) {
            mLiveQualityLooper = new SDSimpleLooper();
        }
        mLiveQualityLooper.start(1000, new Runnable() {
            @Override
            public void run() {
                mBusinessCallback.onBsUpdateLiveQualityData(getLiveQualityData());
            }
        });
    }

    public void stopLiveQualityLooper() {
        if (mLiveQualityLooper != null) {
            mLiveQualityLooper.stop();
        }
    }

    /**
     * 分享
     */
    public void openShare(Activity activity, UMShareListener listener) {
        if (getLiveActivity().getRoomInfo() == null) {
            return;
        }
        final RoomShareModel shareModel = getLiveActivity().getRoomInfo().getShare();
        if (shareModel == null) {
            return;
        }
        UmengSocialManager.openShare(shareModel.getShare_title(), shareModel.getShare_content(), shareModel.getShare_imageUrl(),
                shareModel.getShare_url(), activity, getUMShareListener(listener, shareModel));
    }

    public void doShare(int platform, Activity activity, UMShareListener listener) {
        if (getLiveActivity().getRoomInfo() == null) {
            return;
        }
        final RoomShareModel shareModel = getLiveActivity().getRoomInfo().getShare();
        if (shareModel == null) {
            return;
        }
        UmengSocialManager.doShare(platform, shareModel.getShare_title(), shareModel.getShare_content(), shareModel.getShare_imageUrl(),
                shareModel.getShare_url(), activity, getUMShareListener(listener, shareModel));
    }

    private UMShareListener getUMShareListener(final UMShareListener listener, final RoomShareModel shareModel) {
        return new UMShareListener() {

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA media) {
                LogUtil.i("openShare success");
                String shareKey = shareModel.getShare_key();
                CommonInterface.requestShareComplete(media.toString(), shareKey, new AppRequestCallback<App_shareActModel>() {
                    @Override
                    public String getCancelTag() {
                        return getHttpCancelTag();
                    }

                    @Override
                    protected void onSuccess(SDResponse sdResponse) {
                        if (actModel.isOk()) {
                            //如果分享奖励大于0.提示奖励内容
                            if (actModel.getShare_award() > 0) {
                                CommonInterface.requestMyUserInfo(null);
                                SDToast.showToast(actModel.getShare_award_info());
                            }
                        }
                    }
                });

                if (listener != null) {
                    listener.onResult(media);
                }
            }

            @Override
            public void onError(SHARE_MEDIA media, Throwable throwable) {
                if (listener != null) {
                    listener.onError(media, throwable);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA media) {
                if (listener != null) {
                    listener.onCancel(media);
                }
            }
        };
    }

    /**
     * 请求停止连麦
     *
     * @param userId 要停止的连麦用户id
     */
    public void requestStopLianmai(String userId) {
        CommonInterface.requestStopLianmai(getLiveActivity().getRoomId(), userId, null);
    }

    /**
     * 请求服务端把小主播和大主播视频混流
     *
     * @param userId 要混流的用户id
     */
    public void requestMixStream(String userId) {
        CommonInterface.requestMixStream(getLiveActivity().getRoomId(), userId, null);
    }

    @Override
    protected BaseBusinessCallback getBaseBusinessCallback() {
        return mBusinessCallback;
    }

    public void onDestroy() {
        stopLiveQualityLooper();
        super.onDestroy();
    }

    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg) {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgApplyLinkMic(CustomMsgApplyLinkMic msg) {

    }

    @Override
    public void onMsgAcceptLinkMic(CustomMsgAcceptLinkMic msg) {

    }

    @Override
    public void onMsgRejectLinkMic(CustomMsgRejectLinkMic msg) {

    }

    @Override
    public void onMsgStopLinkMic(CustomMsgStopLinkMic msg) {

    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg) {

    }

    @Override
    public void onMsgStopLive(CustomMsgStopLive msg) {

    }

    @Override
    public void onMsgPrivate(MsgModel msg) {

    }

    @Override
    public void onMsgAuction(MsgModel msg) {

    }

    @Override
    public void onMsgShop(MsgModel msg) {

    }

    @Override
    public void onMsgPayMode(MsgModel msg) {

    }

    @Override
    public void onMsgGame(MsgModel msg) {

    }

    @Override
    public void onMsgGameBanker(CustomMsgGameBanker msg) {

    }

    @Override
    public void onMsgGift(CustomMsgGift msg) {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgPopMsg(CustomMsgPopMsg msg) {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgViewerJoin(CustomMsgViewerJoin msg) {
        insertUser(msg.getSender());
        setViewerNumber(mViewerNumber + 1);
    }

    private void insertUser(UserModel userModel) {
        if (userModel == null) {
            return;
        }
        if (mListViewer.contains(userModel)) {
            return;
        }

        int sortNum = userModel.getSort_num();
        {
            if (sortNum <= 0) {
                sortNum = userModel.getUser_level();
            }
        }

        int count = mListViewer.size();
        int position = 0;
        for (int i = count - 1; i >= 0; i--) {
            UserModel item = mListViewer.get(i);
            int itemNum = item.getUser_level();
            if (sortNum > itemNum) {
                continue;
            } else {
                position = i + 1;
                break;
            }
        }
        LogUtil.i("insert:" + position + ",sortNum:" + sortNum);

        mBusinessCallback.onBsInsertViewer(position, userModel);
    }

    @Override
    public void onMsgViewerQuit(CustomMsgViewerQuit msg) {
        mBusinessCallback.onBsRemoveViewer(msg.getSender());
    }

    @Override
    public void onMsgCreaterLeave(CustomMsgCreaterLeave msg) {

    }

    @Override
    public void onMsgCreaterComeback(CustomMsgCreaterComeback msg) {

    }

    @Override
    public void onMsgLight(CustomMsgLight msg) {

    }

    @Override
    public void onMsgLiveChat(MsgModel msg) {

    }

    @Override
    public void onMsgWarning(CustomMsgWarning msgWarning) {

    }

    @Override
    public void onMsgData(CustomMsgData msg) {
        if (ApkConstant.DEBUG) {
            LogUtil.i("onMsgData " + msg.getData_type() + ":" + msg.getData());
        }
    }

    @Override
    public void onMsgDataViewerList(App_viewerActModel model) {
        if (model.getTime() > mLastViewerListTime) {
            onRequestViewerListSuccess(model);
            mLastViewerListTime = model.getTime();
        }
    }

    @Override
    public void onMsgDataLinkMicInfo(DataLinkMicInfoModel model) {

    }

    @Override
    public void onMsgLargeGift(CustomMsgLargeGift customMsgLargeGift) {

    }

    public interface LiveBusinessCallback extends BaseBusinessCallback {
        /**
         * 请求房间信息成功
         */
        void onBsRequestRoomInfoSuccess(App_get_videoActModel actModel);

        /**
         * 请求房间信息成功，但是业务失败
         */
        void onBsRequestRoomInfoError(App_get_videoActModel actModel);

        /**
         * 请求房间信息异常
         *
         * @param msg
         */
        void onBsRequestRoomInfoException(String msg);

        /**
         * 刷新观众列表
         *
         * @param listModel
         */
        void onBsRefreshViewerList(List<UserModel> listModel);

        /**
         * 移除观众列表中的某个观众
         *
         * @param model
         */
        void onBsRemoveViewer(UserModel model);

        /**
         * 插入某个观众到观众列表
         *
         * @param position 插入位置
         * @param model
         */
        void onBsInsertViewer(int position, UserModel model);

        /**
         * 直播间印票变化
         *
         * @param ticket
         */
        void onBsTicketChange(long ticket);

        /**
         * 直播间观众数量发生变化
         *
         * @param viewerNumber
         */
        void onBsViewerNumberChange(int viewerNumber);

        /**
         * 绑定直播间主播数据
         */
        void onBsBindCreaterData(UserModel model);

        /**
         * 显示隐藏私密直播邀请观众的布局
         *
         * @param show
         */
        void onBsShowOperateViewer(boolean show);

        /**
         * 获得直播间质量数据
         *
         * @return
         */
        LiveQualityData onBsGetLiveQualityData();

        /**
         * 更新直播间质量数据
         *
         * @param data
         */
        void onBsUpdateLiveQualityData(LiveQualityData data);

    }

}
