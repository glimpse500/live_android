package com.oolive.live.business;

import com.oolive.games.model.custommsg.CustomMsgGameBanker;
import com.oolive.library.utils.LogUtil;
import com.oolive.live.LiveConstant;
import com.oolive.live.model.App_viewerActModel;
import com.oolive.live.model.custommsg.CustomMsgAcceptLinkMic;
import com.oolive.live.model.custommsg.CustomMsgApplyLinkMic;
import com.oolive.live.model.custommsg.CustomMsgCreaterComeback;
import com.oolive.live.model.custommsg.CustomMsgCreaterLeave;
import com.oolive.live.model.custommsg.CustomMsgData;
import com.oolive.live.model.custommsg.CustomMsgEndVideo;
import com.oolive.live.model.custommsg.CustomMsgGift;
import com.oolive.live.model.custommsg.CustomMsgLargeGift;
import com.oolive.live.model.custommsg.CustomMsgLight;
import com.oolive.live.model.custommsg.CustomMsgPopMsg;
import com.oolive.live.model.custommsg.CustomMsgPrivateGift;
import com.oolive.live.model.custommsg.CustomMsgPrivateImage;
import com.oolive.live.model.custommsg.CustomMsgPrivateText;
import com.oolive.live.model.custommsg.CustomMsgPrivateVoice;
import com.oolive.live.model.custommsg.CustomMsgRedEnvelope;
import com.oolive.live.model.custommsg.CustomMsgRejectLinkMic;
import com.oolive.live.model.custommsg.CustomMsgStopLinkMic;
import com.oolive.live.model.custommsg.CustomMsgStopLive;
import com.oolive.live.model.custommsg.CustomMsgText;
import com.oolive.live.model.custommsg.CustomMsgViewerJoin;
import com.oolive.live.model.custommsg.CustomMsgViewerQuit;
import com.oolive.live.model.custommsg.CustomMsgWarning;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.live.model.custommsg.data.DataLinkMicInfoModel;
import com.oolive.live.model.custommsg.CustomMsg;
import com.google.gson.Gson;

/**
 * 直播间消息业务类
 */
public class LiveMsgBusiness extends MsgBusiness {
    private LiveMsgBusinessCallback mBusinessCallback;

    public void setBusinessCallback(LiveMsgBusinessCallback businessCallback) {
        this.mBusinessCallback = businessCallback;
    }

    public static CustomMsg json2CustomMsg(String json, int type){
        CustomMsg cMsg = new CustomMsg();
        //int type = msg.getCustomMsgType();
        if (LiveConstant.CustomMsgType.MSG_TEXT == type) {
            LogUtil.i("json to MSG_TEXT");
            cMsg = new Gson().fromJson(json, CustomMsgText.class);
        }
        else if (LiveConstant.CustomMsgType.MSG_RED_ENVELOPE == type) {
            LogUtil.i("json to MSG_RED_ENVELOPE");
            cMsg = new Gson().fromJson(json, CustomMsgRedEnvelope.class);
            //onMsgRedEnvelope(msg);
        } else if (LiveConstant.CustomMsgType.MSG_GIFT == type) {
            LogUtil.i("json to MSG_GIFT");
            cMsg = new Gson().fromJson(json, CustomMsgGift.class);
            //onMsgGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_POP_MSG == type) {
            LogUtil.i("json to cusotomMsgPopMsg");
            cMsg = new Gson().fromJson(json, CustomMsgPopMsg.class);
            //onMsgPopMsg(msg);
        } else if (LiveConstant.CustomMsgType.MSG_VIEWER_JOIN == type) {
            LogUtil.i("json to MSG_VIEWER_JOIN");
            cMsg = new Gson().fromJson(json, CustomMsgViewerJoin.class);
            //onMsgViewerJoin(msg);
        } else if (LiveConstant.CustomMsgType.MSG_VIEWER_QUIT == type) {
            LogUtil.i("json to MSG_VIEWER_QUIT");
            cMsg = new Gson().fromJson(json, CustomMsgViewerQuit.class);
           //onMsgViewerQuit(msg);
        } else if (LiveConstant.CustomMsgType.MSG_CREATER_LEAVE == type) {
            LogUtil.i("json to MSG_CREATER_LEAVE");
            cMsg = new Gson().fromJson(json, CustomMsgCreaterLeave.class);
            //onMsgCreaterLeave(msg);
        } else if (LiveConstant.CustomMsgType.MSG_CREATER_COME_BACK == type) {
            LogUtil.i("json to MSG_CREATER_COME_BACK");
            cMsg = new Gson().fromJson(json, CustomMsgCreaterComeback.class);
           // onMsgCreaterComeback(msg);
        } else if (LiveConstant.CustomMsgType.MSG_LIGHT == type) {
            LogUtil.i("json to MSG_LIGHT");
            cMsg = new Gson().fromJson(json, CustomMsgLight.class);
           // onMsgLight(msg);
        } else if (LiveConstant.CustomMsgType.MSG_END_VIDEO == type) {
            LogUtil.i("json to MSG_END_VIDEO");
            cMsg = new Gson().fromJson(json, CustomMsgEndVideo.class);
            //onMsgEndVideo(msg);
        } else if (LiveConstant.CustomMsgType.MSG_DATA == type) {
            LogUtil.i("json to MSG_DATA");
            cMsg = new Gson().fromJson(json, CustomMsgData.class);
            //onMsgData(msg);
        } else if (LiveConstant.CustomMsgType.MSG_GAME_BANKER == type) {
            LogUtil.i("json to MSG_GAME_BANKER");
            cMsg = new Gson().fromJson(json, CustomMsgGameBanker.class);
            //onMsgGameBanker(msg);
        } else if (LiveConstant.CustomMsgType.MSG_LARGE_GIFT == type) {
            LogUtil.i("json to MSG_LARGE_GIFT");
            cMsg = new Gson().fromJson(json, CustomMsgLargeGift.class);
            //onMsgLargeGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_PRIVATE_TEXT == type) {
            LogUtil.i("json to MSG_PRIVATE_TEXT");
            cMsg = new Gson().fromJson(json, CustomMsgPrivateText.class);

            //onMsgLargeGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_PRIVATE_IMAGE == type) {
            LogUtil.i("json to MSG_LARGE_GIFT");
            cMsg = new Gson().fromJson(json, CustomMsgPrivateImage.class);
            //onMsgLargeGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_PRIVATE_VOICE == type) {
            LogUtil.i("json to MSG_LARGE_GIFT");
            cMsg = new Gson().fromJson(json, CustomMsgPrivateVoice.class);
            //onMsgLargeGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT == type) {
            LogUtil.i("json to MSG_LARGE_GIFT");
            cMsg = new Gson().fromJson(json, CustomMsgPrivateGift.class);
            //onMsgLargeGift(msg);
        }
        return cMsg;
        /*
        //大分类
        if (msg.isAuctionMsg()) {
            //竞拍消息
            onMsgAuction(msg);
        }
        if (msg.isShopPushMsg()) {
            onMsgShop(msg);
        }
        if (msg.isPayModeMsg()) {
            //付费模式消息
            onMsgPayMode(msg);
        }
        if (msg.isGameMsg()) {
            //游戏消息
            onMsgGame(msg);
        }
        if (msg.isLiveChatMsg()) {
            //直播间聊天列表消息
            onMsgLiveChat(msg);
        }*/
    }

    @Override
    protected void onMsgGroup(MsgModel msg) {
        super.onMsgGroup(msg);

        int type = msg.getCustomMsgType();
        if (LiveConstant.CustomMsgType.MSG_RED_ENVELOPE == type) {
            onMsgRedEnvelope(msg);
        } else if (LiveConstant.CustomMsgType.MSG_GIFT == type) {
            onMsgGift(msg);
        } else if (LiveConstant.CustomMsgType.MSG_POP_MSG == type) {
            onMsgPopMsg(msg);
        } else if (LiveConstant.CustomMsgType.MSG_VIEWER_JOIN == type) {
            onMsgViewerJoin(msg);
        } else if (LiveConstant.CustomMsgType.MSG_VIEWER_QUIT == type) {
            onMsgViewerQuit(msg);
        } else if (LiveConstant.CustomMsgType.MSG_CREATER_LEAVE == type) {
            onMsgCreaterLeave(msg);
        } else if (LiveConstant.CustomMsgType.MSG_CREATER_COME_BACK == type) {
            onMsgCreaterComeback(msg);
        } else if (LiveConstant.CustomMsgType.MSG_LIGHT == type) {
            onMsgLight(msg);
        } else if (LiveConstant.CustomMsgType.MSG_END_VIDEO == type) {
            onMsgEndVideo(msg);
        } else if (LiveConstant.CustomMsgType.MSG_DATA == type) {
            onMsgData(msg);
        } else if (LiveConstant.CustomMsgType.MSG_GAME_BANKER == type) {
            onMsgGameBanker(msg);
        } else if (LiveConstant.CustomMsgType.MSG_LARGE_GIFT == type) {
            onMsgLargeGift(msg);
        }

        //大分类
        if (msg.isAuctionMsg()) {
            //竞拍消息
            onMsgAuction(msg);
        }
        if (msg.isShopPushMsg()) {
            onMsgShop(msg);
        }
        if (msg.isPayModeMsg()) {
            //付费模式消息
            onMsgPayMode(msg);
        }
        if (msg.isGameMsg()) {
            //游戏消息
            onMsgGame(msg);
        }
        if (msg.isLiveChatMsg()) {
            //直播间聊天列表消息
            LogUtil.i("live chat message" + msg.getCustomMsg());
            onMsgLiveChat(msg);
        }
    }

    @Override
    protected void onMsgC2C(MsgModel msg) {
        super.onMsgC2C(msg);

        int type = msg.getCustomMsgType();
        if (LiveConstant.CustomMsgType.MSG_STOP_LIVE == type) {
            onMsgStopLive(msg);
        } else if (LiveConstant.CustomMsgType.MSG_APPLY_LINK_MIC == type) {
            onMsgApplyLinkMic(msg);
        } else if (LiveConstant.CustomMsgType.MSG_ACCEPT_LINK_MIC == type) {
            onMsgAcceptLinkMic(msg);
        } else if (LiveConstant.CustomMsgType.MSG_REJECT_LINK_MIC == type) {
            onMsgRejectLinkMic(msg);
        } else if (LiveConstant.CustomMsgType.MSG_STOP_LINK_MIC == type) {
            onMsgStopLinkMic(msg);
        } else if (LiveConstant.CustomMsgType.MSG_WARNING_BY_MANAGER == type) {
            onMsgWarningByManager(msg);
        }

        //大分类
        if (msg.isPrivateMsg()) {
            //私聊消息
            onMsgPrivate(msg);
        }
    }

    /**
     * 点亮消息
     */
    protected void onMsgLight(final MsgModel msg) {
        mBusinessCallback.onMsgLight(msg.getCustomMsgLight());
    }

    /**
     * 主播回来消息
     */
    protected void onMsgCreaterComeback(final MsgModel msg) {
        mBusinessCallback.onMsgCreaterComeback(msg.getCustomMsgCreaterComeback());
    }

    /**
     * 主播离开消息
     */
    protected void onMsgCreaterLeave(final MsgModel msg) {
        mBusinessCallback.onMsgCreaterLeave(msg.getCustomMsgCreaterLeave());
    }

    /**
     * 观众退出消息
     */
    protected void onMsgViewerQuit(final MsgModel msg) {
        mBusinessCallback.onMsgViewerQuit(msg.getCustomMsgViewerQuit());
    }

    /**
     * 观众加入消息
     */
    protected void onMsgViewerJoin(final MsgModel msg) {
        mBusinessCallback.onMsgViewerJoin(msg.getCustomMsgViewerJoin());
    }

    /**
     * 弹幕消息
     */
    protected void onMsgPopMsg(final MsgModel msg) {
        mBusinessCallback.onMsgPopMsg(msg.getCustomMsgPopMsg());
    }

    /**
     * 礼物消息
     */
    protected void onMsgGift(final MsgModel msg) {
        mBusinessCallback.onMsgGift(msg.getCustomMsgGift());
    }

    /**
     * 红包消息
     */
    protected void onMsgRedEnvelope(final MsgModel msg) {
        mBusinessCallback.onMsgRedEnvelope(msg.getCustomMsgRedEnvelope());
    }

    /**
     * 申请连麦
     */
    protected void onMsgApplyLinkMic(final MsgModel msg) {
        mBusinessCallback.onMsgApplyLinkMic(msg.getCustomMsgApplyLinkMic());
    }

    /**
     * 接受连麦
     */
    protected void onMsgAcceptLinkMic(final MsgModel msg) {
        mBusinessCallback.onMsgAcceptLinkMic(msg.getCustomMsgAcceptLinkMic());
    }

    /**
     * 拒绝连麦
     */
    protected void onMsgRejectLinkMic(final MsgModel msg) {
        mBusinessCallback.onMsgRejectLinkMic(msg.getCustomMsgRejectLinkMic());
    }

    /**
     * 用户结束连麦
     */
    protected void onMsgStopLinkMic(final MsgModel msg) {
        mBusinessCallback.onMsgStopLinkMic(msg.getCustomMsgStopLinkMic());
    }

    protected void onMsgWarningByManager(final MsgModel msg) {
        LogUtil.i(msg.getCustomMsgWarning().getDesc());
        mBusinessCallback.onMsgWarning(msg.getCustomMsgWarning());
    }

    /**
     * 直播结束
     */
    protected void onMsgEndVideo(final MsgModel msg) {
        mBusinessCallback.onMsgEndVideo(msg.getCustomMsgEndVideo());
    }

    /**
     * 关闭当前直播
     */
    protected void onMsgStopLive(final MsgModel msg) {
        mBusinessCallback.onMsgStopLive(msg.getCustomMsgStopLive());
    }

    /**
     * 通用数据消息
     *
     * @param msg
     */
    protected void onMsgData(final MsgModel msg) {
        CustomMsgData msgData = msg.getCustomMsgData();
        mBusinessCallback.onMsgData(msgData);

        // 对通用数据消息进行分发回调
        int dataType = msgData.getData_type();
        switch (dataType) {
            case LiveConstant.CustomMsgDataType.DATA_VIEWER_LIST:
                App_viewerActModel actModel = msgData.parseData(App_viewerActModel.class);
                if (actModel != null) {
                    actModel.parseData();
                    mBusinessCallback.onMsgDataViewerList(actModel);
                }
                break;
            case LiveConstant.CustomMsgDataType.DATA_LINK_MIC_INFO:
                DataLinkMicInfoModel dataLinkMicInfoModel = msgData.parseData(DataLinkMicInfoModel.class);
                if (dataLinkMicInfoModel != null) {
                    mBusinessCallback.onMsgDataLinkMicInfo(dataLinkMicInfoModel);
                }
                break;
            default:
                break;
        }
    }

    // 大分类消息

    /**
     * 私聊消息
     */
    protected void onMsgPrivate(final MsgModel msg) {
        mBusinessCallback.onMsgPrivate(msg);
    }

    /**
     * 竞拍消息
     */
    protected void onMsgAuction(final MsgModel msg) {
        mBusinessCallback.onMsgAuction(msg);
    }

    /**
     * 购物消息
     */
    protected void onMsgShop(MsgModel msg) {
        mBusinessCallback.onMsgShop(msg);
    }

    /**
     * 付费消息
     */
    protected void onMsgPayMode(final MsgModel msg) {
        mBusinessCallback.onMsgPayMode(msg);
    }

    /**
     * 游戏消息
     */
    protected void onMsgGame(final MsgModel msg) {
        mBusinessCallback.onMsgGame(msg);
    }

    /**
     * 游戏上庄消息
     */
    protected void onMsgGameBanker(final MsgModel msg) {
        mBusinessCallback.onMsgGameBanker(msg.getCustomMsgGameBanker());
    }

    /**
     * 大型礼物动画通知
     */
    protected void onMsgLargeGift(final MsgModel msg) {
        mBusinessCallback.onMsgLargeGift(msg.getCustomMsgLargeGift());
    }

    /**
     * 直播间聊天列表消息
     */
    protected void onMsgLiveChat(final MsgModel msg) {
        mBusinessCallback.onMsgLiveChat(msg);
    }

    public interface LiveMsgBusinessCallback {
        /**
         * 红包消息
         */
        void onMsgRedEnvelope(CustomMsgRedEnvelope msg);

        /**
         * 申请连麦
         */
        void onMsgApplyLinkMic(CustomMsgApplyLinkMic msg);

        /**
         * 接受连麦
         */
        void onMsgAcceptLinkMic(CustomMsgAcceptLinkMic msg);

        /**
         * 拒绝连麦
         */
        void onMsgRejectLinkMic(CustomMsgRejectLinkMic msg);

        /**
         * 用户结束连麦
         */
        void onMsgStopLinkMic(CustomMsgStopLinkMic msg);

        /**
         * 直播结束
         */
        void onMsgEndVideo(CustomMsgEndVideo msg);

        /**
         * 关闭当前直播
         */
        void onMsgStopLive(CustomMsgStopLive msg);

        /**
         * 私聊消息
         */
        void onMsgPrivate(MsgModel msg);

        /**
         * 竞拍消息
         */
        void onMsgAuction(MsgModel msg);

        /**
         * 购物消息
         */
        void onMsgShop(MsgModel msg);

        /**
         * 付费消息
         */
        void onMsgPayMode(MsgModel msg);

        /**
         * 游戏消息
         */
        void onMsgGame(MsgModel msg);

        /**
         * 上庄消息
         *
         * @param msg
         */
        void onMsgGameBanker(CustomMsgGameBanker msg);

        /**
         * 礼物消息
         */
        void onMsgGift(CustomMsgGift msg);

        /**
         * 弹幕消息
         */
        void onMsgPopMsg(CustomMsgPopMsg msg);

        /**
         * 观众加入消息
         */
        void onMsgViewerJoin(CustomMsgViewerJoin msg);

        /**
         * 观众退出消息
         */
        void onMsgViewerQuit(CustomMsgViewerQuit msg);

        /**
         * 主播离开消息
         */
        void onMsgCreaterLeave(CustomMsgCreaterLeave msg);

        /**
         * 主播回来消息
         */
        void onMsgCreaterComeback(CustomMsgCreaterComeback msg);

        /**
         * 点亮消息
         */
        void onMsgLight(CustomMsgLight msg);

        /**
         * 直播间聊天列表消息
         */
        void onMsgLiveChat(MsgModel msg);

        /**
         * 直播间内警告消息
         */
        void onMsgWarning(CustomMsgWarning msg);

        /**
         * 数据消息
         *
         * @param msg
         */
        void onMsgData(CustomMsgData msg);

        /**
         * 直播间观众列表消息
         */
        void onMsgDataViewerList(App_viewerActModel model);

        /**
         * 直播间当前连麦的用户信息和连麦的信息消息
         *
         * @param model
         */
        void onMsgDataLinkMicInfo(DataLinkMicInfoModel model);

        /**
         * 大型礼物动画通知
         *
         * @param customMsgLargeGift
         */
        void onMsgLargeGift(CustomMsgLargeGift customMsgLargeGift);
    }

}
