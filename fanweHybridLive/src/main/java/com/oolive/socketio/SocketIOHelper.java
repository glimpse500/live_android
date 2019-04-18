package com.oolive.socketio;

import android.app.Activity;
import android.text.TextUtils;


import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.model.InitActModel;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.LiveConstant;
import com.oolive.live.business.LiveMsgBusiness;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EImOnNewMessages;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.ConversationUnreadMessageModel;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.User_is_blackActModel;
import com.oolive.live.model.custommsg.CustomMsgPopMsg;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.socketio.SocketIOConstant;
import com.sunday.eventbus.SDEventManager;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.CustomMsg;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SocketIOHelper {
    private static Socket mSocket;
    private static boolean isInLogin = false;
    private static int numUsers;
    private static String cur_group = "-1";
    private static Activity activity = null;
    private static Boolean isConnected = false;
    private static String mUsername = null;
    private static String mUserID = null;
    private static Emitter.Listener onLogin = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                numUsers = -1;
                return;
            }

        }
    };

    public static String getUserID(){
        return mUserID;
    }
    public static String getConversationKey(String peer){
        return getUserID() + "_" + peer;
    }
    public static void loginSocketIO(String userId, String userSig,Activity act) {
        if (isInLogin) {
            return;
        }
        if (activity == null)
            activity = act;
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel == null) {
            LogUtil.e("login  error because of null InitActModel");
            return;
        }
        try {
            LogUtil.i("try to connet to SocketIO server : " + SocketIOConstant.CHAT_SERVER_URL);
            mSocket = IO.socket(SocketIOConstant.CHAT_SERVER_URL);
            mSocket.on(Socket.EVENT_CONNECT,onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            IO.Options opts = new IO.Options();

            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on("login", onLogin);
        mSocket.on("new message", onNewMessage);
        LogUtil.i("login : ");
        mSocket.emit("add user", userId);
        LogUtil.i("add user : " + userId);
        mUsername = userSig;
        mUserID = userId;
        isConnected = true;
        isInLogin = true;
    }
    public static void logoutSocketIO(){
        mSocket.off("login", onLogin);
    }
    public static void joinGroup(String room_id){
        LogUtil.i("join + " +  room_id);
        if (ApkConstant.DEBUG) {
            room_id = "8888";
            LogUtil.i("join (debug) + " +  room_id);
        }
        if (!room_id.equals(cur_group)) {
            LogUtil.i("leave + " +  cur_group);
            mSocket.emit("leave", cur_group);
            cur_group = room_id;
            LogUtil.i("join + " +  room_id);
            mSocket.emit("join", room_id);

        }

    }
    public static List<MsgModel> getC2CMsgList(Activity activity) {
        LogUtil.i("getC2CMsgList");
        List<MsgModel> listMsg = new ArrayList<>();
        UserModel user = UserModelDao.query();
        if (user != null) {
            long count = SocketIOManager.getInstance().getConversationCount();
            LogUtil.i("count = " + count);
            for (int i = 0; i < count; ++i) {
                SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(i);
                LogUtil.i("conversation.getType()  = " + conversation.getType());
                if (SocketIOConversationType.C2C == conversation.getType()) {
                    // 自己对自己发的消息过滤
                    LogUtil.i("conversation.getPeer()  = " + conversation.getPeer() + " : " + user.getUser_id());
                    if (!conversation.getPeer().equals(user.getUser_id())) {
                        List<SocketIOMessage> list = conversation.getLastMsgs(1);
                        LogUtil.i("list get i "+ list.get(i).getJson());
                        if (list != null && list.size() > 0) {
                            SocketIOMessage sMsg = list.get(0);
                            MsgModel msg = new LiveMsgModel(sMsg);
                            msg.setConversationPeer(conversation.getPeer());
                            if (msg.isPrivateMsg()) {
                                listMsg.add(msg);
                            }
                        }
                    }
                }
            }
        }
        return listMsg;
    }
    public static void sendMsgGroup(String id, CustomMsg customMsg) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        //String groupId = id;
        if (ApkConstant.DEBUG)
            id = "8888";
        LogUtil.i("sendMsgGroup + "+ customMsg.parsetoSocketIOMessage().getJson() );
        mSocket.emit("new group_msg",id,customMsg.parsetoSocketIOMessage().getJson());
        MsgModel msg = customMsg.parseToMsgModel();
        if (msg != null) {
            msg.setCustomMsg(customMsg);
            msg.setLocalPost(true);
            msg.setSelf(true);
            EImOnNewMessages event = new EImOnNewMessages();
            event.msg = msg;
            SDEventManager.post(event);
        }

    }
    public static boolean connected(){return isConnected;}


    public  static SocketIOConversation getConversationGroup(String id) {
        SocketIOConversation conversation = null;
        if (!TextUtils.isEmpty(id)) {
            conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.Group, id);
        }
        return conversation;
    }
    public static void sendMsgC2C(final String id,final CustomMsg customMsg,final SocketIOValueCallBack<SocketIOMessage> callback) {
        if (TextUtils.isEmpty(id)) {
            SDToast.showToast("無聊天對象");
            callback.onError(0,"無聊天對象");
        }
        else if (!isConnected) {
            SDToast.showToast("聊天室異常");
            callback.onError(0,"聊天室異常");
        }
        else{
            CommonInterface.requestIs_black(customMsg.getSender().getUser_id(), new AppRequestCallback<User_is_blackActModel>() {
                protected void onSuccess(SDResponse resp) {
                    if (actModel.isOk() && !actModel.getIs_black()) {

                        SocketIOMessage socketioMsg = customMsg.parsetoSocketIOMessage();


                        mSocket.emit("c2c_msg",id,customMsg.parsetoSocketIOMessage().getJson());
                        callback.onSuccess(socketioMsg);
                        LogUtil.i("socketioMsg " + socketioMsg.getJson());
                    }
                    else {
                        callback.onError(1,"無法傳送訊息");
                    }
                }

            });
        }

    }
    public static SocketIOConversation getConversationC2C(String id) {
        SocketIOConversation conversation = null;
        if (!TextUtils.isEmpty(id)) {
            conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.C2C, id);
        }
        return conversation;
    }
    private static Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            //String username;
            String json_message;
            try {
                //username = data.getString("username");
                json_message = data.getString("message");
                LogUtil.i("on json_message  :" + json_message);
                JSONObject message_body = new JSONObject(json_message);
                LogUtil.i("on message_body  :" + message_body);
                CustomMsg cMsg = LiveMsgBusiness.json2CustomMsg(json_message,Integer.valueOf(message_body.getString("type")));
                SocketIOMessage sMsg = cMsg.parsetoSocketIOMessage();
                MsgModel msg = new LiveMsgModel(sMsg);
                if (msg != null) {

                    msg.setCustomMsg(cMsg);
                    msg.setConversationPeer(cMsg.getSender().getUser_id());
                    EImOnNewMessages event = new EImOnNewMessages();
                    SocketIOConversation conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.C2C,cMsg.getSender().getUser_id());
                    if (msg.isPrivateMsg()){
                        msg.setTimestamp(cMsg.getTime_stamp());
                        sMsg.setTimeStamp(cMsg.getTime_stamp());
                        conversation.writeLocalMessage(sMsg,activity,false);
                        SocketIOManager.getInstance().postERefreshMsgUnReaded(true);
                    }
                    //msg.setSelf(false);
                    event.msg = msg;
                    LogUtil.i("SDEventManager.post(event);  :");
                    SDEventManager.post(event);
                }
            }
            catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
                return;
            }
        }
    };
    private static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername){
                            mSocket.emit("add user", mUserID);
                            LogUtil.i("cur_group" + cur_group);
                            if (!cur_group.equals("-1"))
                                joinGroup(cur_group);
                        }
                        SDToast.showToast("連接聊天室成功 (加入房間)");
                        isConnected = true;
                    }
                }
            });
        }
    };

    private static Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    //cur_group = "-1";
                    SDToast.showToast("嘗試重新連接聊天室...");
                }
            });
        }
    };

    private static Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                }
            });
        }
    };
}
