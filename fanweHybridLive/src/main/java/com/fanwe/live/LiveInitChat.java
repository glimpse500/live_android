package com.fanwe.live;

import android.app.Application;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.lib.player.SDMediaPlayer;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.event.EImOnForceOffline;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.event.EImOnRefresh;
import com.fanwe.live.event.ESDMediaPlayerStateChanged;
import com.fanwe.live.model.custommsg.MsgModel;
import com.fanwe.live.model.custommsg.TIMMsgModel;
import com.fanwe.socketio.*;


import com.sunday.eventbus.SDEventManager;
import com.fanwe.live.model.custommsg.LiveMsgModel;


import java.util.List;

public class LiveInitChat {

    public void init(Application app) {
        SocketIOManager.getInstance().addMessageListener(new SocketIOListener() {
            private void postNewMessage(MsgModel msgModel) {
                EImOnNewMessages event = new EImOnNewMessages();
                event.msg = msgModel;
                SDEventManager.post(event);
                if (msgModel.isPrivateMsg()) {
                    SocketIOHelper.postERefreshMsgUnReaded();
                }
            }
            @Override
            public boolean onNewMessages(final List<SocketIOMessage> listMessage) {
                if (!SDCollectionUtil.isEmpty(listMessage)) {
                    SDHandlerManager.getBackgroundHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            for (SocketIOMessage msg : listMessage) {
                                if (ApkConstant.DEBUG) {
                                    SocketIOConversation conversation = msg.getConversation();
                                    LogUtil.i("--------receive msg:" + conversation.getType() + " " + conversation.getPeer());
                                }

                                boolean post = true;
                                final LiveMsgModel msgModel = new LiveMsgModel(msg);

                                if (msgModel.getConversationPeer().equals(LiveInformation.getInstance().getCurrentChatPeer())) {
                                    IMHelper.setSingleC2CReadMessage(msgModel.getConversationPeer(), false);
                                }
                                //To do, currently no sound support
                                /*
                                boolean needDownloadSound = msgModel.checkSoundFile(new TIMValueCallBack<String>() {
                                    @Override
                                    public void onError(int i, String s) {
                                    }

                                    @Override
                                    public void onSuccess(String path) {
                                        msgModel.getCustomMsgPrivateVoice().setPath(path);
                                        postNewMessage(msgModel);
                                    }
                                });
                                if (needDownloadSound) {
                                    post = false;
                                }*/


                                if (post) {
                                    postNewMessage(msgModel);
                                }
                            }
                        }
                    });
                }
                return false;
            }
        });


        SocketIOManager.getInstance().setRefreshListener(new SocketIORefreshListener() {

            @Override
            public void onRefresh() {
                // 默认登陆后会异步获取离线消息以及同步资料数据（如果有开启ImSDK存储，可参见关系链资料章节），同步完成后会通过onRefresh回调通知更新界面，用户得到这个消息时，可以刷新界面，比如会话列表的未读等等：
                SDEventManager.post(new EImOnRefresh());
            }

            @Override
            public void onRefreshConversation(List<SocketIOConversation> list) {
            }
        });
        SocketIOManager.getInstance().setUserStatusListener(new SocketIOUserStatusListener() {
            @Override
            public void onForceOffline() {
                SDEventManager.post(new EImOnForceOffline());
            }

            @Override
            public void onUserSigExpired() {
                CommonInterface.requestUsersig(null);
            }
        });

        SocketIOManager.getInstance().setLogLevel(SocketIOLogLevel.OFF);
        SocketIOManager.getInstance().init(app);

        SDMediaPlayer.getInstance().setOnStateChangeCallback(new SDMediaPlayer.OnStateChangeCallback() {
            @Override
            public void onStateChanged(SDMediaPlayer.State oldState, SDMediaPlayer.State newState, SDMediaPlayer player) {
                LogUtil.i("onStateChanged:" + newState);
                SDEventManager.post(new ESDMediaPlayerStateChanged(newState));
            }
        });
    }

}
