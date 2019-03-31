package com.oolive.live.business;

import com.oolive.chat.ChatSDKHelper;
import com.oolive.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.receiver.SDNetworkReceiver;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDCollectionUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.IMHelper;
import com.oolive.live.LiveInformation;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EImOnNewMessages;
import com.oolive.live.model.App_userinfoActModel;
import com.oolive.live.model.Deal_send_propActModel;
import com.oolive.live.model.LiveGiftModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.custommsg.CustomMsgPrivateGift;
import com.oolive.live.model.custommsg.CustomMsgPrivateImage;
import com.oolive.live.model.custommsg.CustomMsgPrivateText;
import com.oolive.live.model.custommsg.CustomMsgPrivateVoice;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.live.model.custommsg.MsgStatus;
import com.oolive.socketio.SocketIOConversation;
import com.oolive.socketio.SocketIOConversationType;
import com.oolive.socketio.SocketIOHelper;
import com.oolive.socketio.SocketIOManager;
import com.oolive.socketio.SocketIOMessage;
import com.oolive.socketio.SocketIOValueCallBack;
import com.tencent.TIMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;

import co.chatsdk.core.dao.Message;
import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.events.EventType;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 私聊业务类
 */
public class LivePrivateChatBusiness extends BaseBusiness {
    public LivePrivateChatBusiness(Activity activity,LivePrivateChatBusinessCallback callback) {
        mCallback = callback;
        mActivity = activity;

    }

    private TIMMessage mLastMsg;
    private SocketIOMessage mLastsMsg;
    /**
     * 私聊的用户id
     */
    private String mUserId;
    private String mChatId;
    private co.chatsdk.core.dao.Thread c2cThread;
    private static Disposable msgListener;
    private Activity mActivity;
    private LivePrivateChatBusinessCallback mCallback;

    /**
     * 设置私聊用户id
     *
     * @param userId
     */
    public void setUserId(String userId) {
        mUserId = userId;
        LiveInformation.getInstance().setCurrentChatPeer(userId);
    }

    public String getUserId() {
        return mUserId;
    }

    public void setChatId(String mChatId) {
        this.mChatId = mChatId;
        LiveInformation.getInstance().setCurrentChatPeer(mChatId);
    }

    public String getChatId() {
        return mChatId;
    }
    /**
     * 设置最后一条im消息，加载历史消息的时候会从从最后一条消息往前加载
     *
     * @param lastMsg
     */
    public void setLastMsg(SocketIOMessage lastMsg) {
        mLastsMsg = lastMsg;
    }

    /**
     * 设置最后一条im消息，加载历史消息的时候会从从最后一条消息往前加载
     *
     * @param lastMsg
     */
    /*
    public void setLastMsg(TIMMessage lastMsg) {
        mLastMsg = lastMsg;
    }*/




    /**
     *
     * 是否可以发私信
     *
     * @return
     */
    public boolean canSendPrivateLetter() {
        UserModel userModel = UserModelDao.query();
        if (userModel != null) {
            return userModel.canSendPrivateLetter();
        } else {
            return true;
        }
    }


    /**
     * 请求用户信息
     */
    public void requestUserInfo() {
        CommonInterface.requestUserInfo(null, mUserId, new AppRequestCallback<App_userinfoActModel>() {
            @Override
            public String getCancelTag() {
                return getHttpCancelTag();
            }

            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.isOk()) {
                    setChatId(actModel.getUser().getChat_id());
                    LogUtil.i("requestUserInfo setChatId" + actModel.getUser().getChat_id());
                    mCallback.onRequestUserInfoSuccess(actModel.getUser());
                    User peer = ChatSDK.db().fetchUserWithEntityID(actModel.getUser().getChat_id());
                    List<User> user_list = new ArrayList<>();
                    user_list.add(ChatSDK.currentUser());
                    user_list.add(peer);
                    c2cThread = ChatSDK.db().fetchThreadWithUsers(user_list);
                    if (c2cThread == null){
                        ChatSDK.thread().createThread("c2cMsgThread", user_list)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> {
                                    // Runs when process completed with error or success
                                })
                                .subscribe(thread -> {
                                   c2cThread = thread;
                                }, throwable -> {
                                    SDToast.showToast("創建私聊異常");
                                });
                    }

                } else {

                }
            }
        });
    }

    /**
     * 请求发送礼物
     *
     * @param model
     */
    public void requestSendGiftPrivate(final LiveGiftModel model) {
        CommonInterface.requestSendGiftPrivate(model.getId(), 1, mUserId, new AppRequestCallback<Deal_send_propActModel>() {
            @Override
            public String getCancelTag() {
                return getHttpCancelTag();
            }

            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.isOk()) {
                    mCallback.onRequestSendGiftPrivateSuccess(actModel, model);
                }
            }
        });
    }
    /**
     * 加载历史消息
     *
     * @param count
     */

    public void loadHistoryMessage(int count) {
        SocketIOConversation conversation = SocketIOHelper.getConversationC2C(mUserId);
        if (conversation == null) {
            return;
        }
        final List<MsgModel> listLocal = new ArrayList<>();
        conversation.getLocalMessage(count, mLastsMsg, mActivity,new SocketIOValueCallBack<List<SocketIOMessage>>() {
            @Override
            public void onSuccess(List<SocketIOMessage> list) {
                LogUtil.i(" loadHistoryMessage onSuccess = ");
                if (!SDCollectionUtil.isEmpty(list)) {
                    Collections.reverse(list);
                    setLastMsg(list.get(0));
                    for (SocketIOMessage msg : list) {
                        MsgModel msgModel = new LiveMsgModel(msg);
                        if (msgModel.isPrivateMsg() && msgModel.getStatus() != MsgStatus.HasDeleted) {
                            listLocal.add(msgModel);
                        }
                    }
                }
                mCallback.onLoadHistoryMessageSuccess(listLocal);
            }

            @Override
            public void onError(int arg0, String str) {
                LogUtil.i(" loadHistoryMessage onError = " + str);
                mCallback.onLoadHistoryMessageError();
            }
        });
    }
    /**
     * 加载历史消息
     *
     * @param count
     */
    /*
    public void loadHistoryMessage(int count) {
        TIMConversation conversation = IMHelper.getConversationC2C(mUserId);
        if (conversation == null) {
            return;
        }
        final List<MsgModel> listLocal = new ArrayList<>();
        conversation.getLocalMessage(count, mLastMsg, new TIMValueCallBack<List<TIMMessage>>() {

            @Override
            public void onSuccess(List<TIMMessage> list) {
                if (!SDCollectionUtil.isEmpty(list)) {
                    Collections.reverse(list);
                    setLastMsg(list.get(0));

                    for (TIMMessage msg : list) {
                        MsgModel msgModel = new TIMMsgModel(msg);
                        if (msgModel.isPrivateMsg() && msgModel.getStatus() != MsgStatus.HasDeleted) {
                            listLocal.add(msgModel);
                        }
                    }
                }
                mCallback.onLoadHistoryMessageSuccess(listLocal);
            }

            @Override
            public void onError(int arg0, String str) {
                mCallback.onLoadHistoryMessageError();
            }
        });
    }*/

    /**
     * 接收IM新消息的时候调用
     *
     * @param event
     */
    public void onEventMainThread(EImOnNewMessages event) {
        // 判断新消息来源是否是当前用户
        LogUtil.i("onEventMainThread" + event.msg.getConversationPeer());
        LogUtil.i("mUserId" + mUserId);
        LogUtil.i("event.msg.isPrivateMsg() " + event.msg.isPrivateMsg());
        LogUtil.i("event.msg.getCustomMsgType() " + event.msg.getCustomMsgType() );
        if (event.msg.getConversationPeer().equals(mUserId)) {
            if (event.msg.isPrivateMsg()) {
                mCallback.onAdapterAppendData(event.msg);
            }
        }
    }

    /**
     * 发送IM文字
     *
     * @param content
     */
    public void sendIMText(String content) {
        CustomMsgPrivateText msg = new CustomMsgPrivateText();
        msg.setText(content);
        msg.setUser_id(mUserId);
        msg.setChat_id(mChatId);
        MsgModel msgModel = msg.parseToMsgModel();
        //msgModel.setConversationPeer();
        mCallback.onAdapterAppendData(msgModel);
        LogUtil.i("Send IM Text " + content);
        sendIMMsg(msgModel);
    }

    /**
     * 发送IM图片
     *
     * @param file
     */
    public void sendIMImage(File file) {
        CustomMsgPrivateImage msg = new CustomMsgPrivateImage();
        msg.setPath(file.getAbsolutePath());
        MsgModel msgModel = msg.parseToMsgModel();
        mCallback.onAdapterAppendData(msgModel);

        sendIMMsg(msgModel);
    }

    /**
     * 发送IM语音
     *
     * @param file
     * @param duration
     */
    public void sendIMVoice(File file, long duration) {
        CustomMsgPrivateVoice msg = new CustomMsgPrivateVoice();
        msg.setDuration(duration);
        msg.setPath(file.getAbsolutePath());

        MsgModel msgModel = msg.parseToMsgModel();
        mCallback.onAdapterAppendData(msgModel);

        sendIMMsg(msgModel);
    }

    /**
     * 发送IM礼物
     *
     * @param model
     */
    public void sendIMGift(Deal_send_propActModel model) {
        CustomMsgPrivateGift msg = new CustomMsgPrivateGift();

        msg.fillData(model);

        MsgModel msgModel = msg.parseToMsgModel();

        mCallback.onAdapterAppendData(msgModel);

        sendIMMsg(msgModel);
    }

    public void sendIMMsg(final MsgModel model) {
        final int index = mCallback.onAdapterIndexOf(model);

        ChatSDKHelper.sendMsgC2C(mUserId,c2cThread,model.getCustomMsg(),new SocketIOValueCallBack<SocketIOMessage>() {
            @Override
            public void onSuccess(SocketIOMessage sMsg) {
                SocketIOConversation conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.C2C,mUserId);
                if (mLastsMsg == null) {
                    mLastsMsg = sMsg;
                }
                if (model.getStatus() == MsgStatus.SendFail) {
                    model.remove();
                }
                else{
                    conversation.writeLocalMessage(sMsg,mActivity);
                }
                //TO DO
                //model.setTimMessage(timMessage);

                mCallback.onAdapterUpdateData(index, model);
            }

            @Override
            public void onError(int arg0, String arg1) {
                mCallback.onAdapterUpdateData(index);
            }
        });

        //TO DO
        /*
        TIMMessage timMessageSending = IMHelper.sendMsgC2C(mUserId, model.getCustomMsg(), new TIMValueCallBack<TIMMessage>() {

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (mLastMsg == null) {
                    mLastMsg = timMessage;
                }

                if (model.getStatus() == MsgStatus.SendFail) {
                    model.remove();
                }


                //model.setTimMessage(timMessage);

                mCallback.onAdapterUpdateData(index, model);
            }

            @Override
            public void onError(int arg0, String arg1) {
                mCallback.onAdapterUpdateData(index);
            }
        });

       model.setTimMessage(timMessageSending);
        */

        mCallback.onAdapterUpdateData(index, model);

    }

    @Override
    protected BaseBusinessCallback getBaseBusinessCallback() {
        return mCallback;
    }

    public void onDestroy() {
        IMHelper.setSingleC2CReadMessage(mUserId);
        LiveInformation.getInstance().setCurrentChatPeer("");
        super.onDestroy();
    }

    public interface LivePrivateChatBusinessCallback extends BaseBusinessCallback {

        /**
         * 请求用户信息接口成功
         *
         * @param userModel
         */
        void onRequestUserInfoSuccess(UserModel userModel);

        /**
         * 请求私聊发送礼物接口成功
         *
         * @param actModel
         */
        void onRequestSendGiftPrivateSuccess(Deal_send_propActModel actModel, LiveGiftModel giftModel);

        /**
         * 加载历史消息成功
         *
         * @param listMsg
         */
        void onLoadHistoryMessageSuccess(List<MsgModel> listMsg);

        /**
         * 加载历史消息失败
         */
        void onLoadHistoryMessageError();

        void onAdapterAppendData(MsgModel model);

        void onAdapterUpdateData(int position, MsgModel model);

        void onAdapterUpdateData(int position);

        int onAdapterIndexOf(MsgModel model);
    }

}
