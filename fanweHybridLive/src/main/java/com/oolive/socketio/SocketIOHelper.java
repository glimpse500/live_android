package com.oolive.socketio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.utils.LogUtil;
import com.oolive.live.business.LiveMsgBusiness;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EImOnNewMessages;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.ConversationUnreadMessageModel;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.User_is_blackActModel;
import com.oolive.live.model.custommsg.MsgModel;
import com.sunday.eventbus.SDEventManager;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.CustomMsg;

import org.json.JSONException;
import org.json.JSONObject;

import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.oolive.library.utils.SDToast;

public class SocketIOHelper {
    private static Socket mSocket;
    private static boolean isInLogin = false;
    private static int numUsers;
    private static String cur_group = "-1";
    private static Activity activity = null;
    private static Boolean isConnected = false;
    private static String mUsername = null;
    private static String mUserID = null;
    private static String roomID;
    private static User chatSdkUser_self = null;
    private static ChatSDK chatSDK;
    private static final String password = "oolive_pwd";
    protected ProgressDialog progressDialog;
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
    public static void init(Context context){

        // The Chat SDK needs access to the application's context

        try {
            // Create a new configuration
            Configuration.Builder builder = new Configuration.Builder(context);
            // Perform any other configuration steps (optional)
            builder.firebaseRootPath("prod");
            // Initialize the Chat SDK
            ChatSDK.initialize(builder.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));
            // File storage is needed for profile image upload and image messages

            // Push notification module
            //FirebasePushModule.activate();
            // Activate any other modules you need.
            // ...

        } catch (ChatSDKException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        // File storage is needed for profile image upload and image messages
        FirebaseFileStorageModule.activate();

        // Uncomment this to enable Firebase UI
        // FirebaseUIModule.activate(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID);
    }
    public static String getUserID(){
        return mUserID;
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

        String username = "user_" + userId + "@oolive.com";
        String userID = "user@" +userId;
        //login catch
        if (ChatSDK.auth().isAuthenticatedThisSession()) {
            LogUtil.i("登入成功!! ");
            SDToast.showToast("登入成功!! ");
        } else if (ChatSDK.auth().isAuthenticated()) {
            Disposable d = ChatSDK.auth().authenticate().subscribe(() -> {
                LogUtil.i("登入成功!! ");
                SDToast.showToast("登入成功!! ");
            }, throwable -> {
                loginChatSDK(userID,username);
            });
        } else {
            loginChatSDK(userID,username);
        }
        //ChatSDK.currentUser().setEntityID(userID);
        //ChatSDK.currentUser().update();
        //LogUtil.i("chatSdkUserId + " +  ChatSDK.currentUser().getEntityID());
        isConnected = true;
        isInLogin = true;
    }
    public static void loginChatSDK(String userID,String username){
        AccountDetails details = new AccountDetails();
        details.type = AccountDetails.Type.Username;
        details.username = username;
        details.password = password;
        Disposable d = ChatSDK.auth().authenticate(details).subscribe(() -> {
            ChatSDK.currentUser().setEntityID(userID);
            LogUtil.i("登入成功!! (帳號) " );
            SDToast.showToast("登入成功!! (帳號)");

        }, throwable -> {
            LogUtil.i("註冊聊天帳號 " );
            SDToast.showToast("註冊聊天帳號" );
            details.type = AccountDetails.Type.Register;
            ChatSDK.auth().authenticate(details).subscribe(() -> {
                ChatSDK.currentUser().setEntityID(userID);
                LogUtil.i("註冊聊天帳號成功 " );
                SDToast.showToast("註冊聊天帳號成功" );
            }, throwable2 -> {
                LogUtil.i("註冊聊天帳號失敗 " );
                SDToast.showToast("註冊聊天帳號失敗");
            });
        });
        LogUtil.i("details.type!!"  + details.type );
    }
    public static void logout(){
        ChatSDK.auth().logout();
    }
    public static void logoutSocketIO(){
        mSocket.off("login", onLogin);
    }
    public static void postERefreshMsgUnReaded() {
        postERefreshMsgUnReaded(false);
    }

    public static void postERefreshMsgUnReaded(boolean isFromSetLocalReade) {
        ERefreshMsgUnReaded event = new ERefreshMsgUnReaded();
        event.model = SocketIOHelper.getC2CTotalUnreadMessageModel(activity);
        event.isFromSetLocalReaded = isFromSetLocalReade;
        SDEventManager.post(event);
    }
    public static void joinGroup(String room_id) {
        roomID = "room@" + room_id;
        LogUtil.i("chatSdkUser_self ID = " + chatSdkUser_self.getId());
        Disposable d = ChatSDK.thread().createThread(roomID, chatSdkUser_self)
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {

                })
                .subscribe(thread -> {
                    SDToast.showToast("創建房間成功!!" + thread.getEntityID());
                    thread.setEntityID(roomID);
                    thread.update();
                }, throwable -> {
                    SDToast.showToast("創建房間失敗!! " + throwable.toString());
                });


    }
    public static List<MsgModel> getC2CMsgList(Activity activity) {
        LogUtil.i("getC2CMsgList");
        List<MsgModel> listMsg = new ArrayList<>();
        UserModel user = UserModelDao.query();
        if (user != null) {
            long count = SocketIOManager.getInstance().getConversationCount(activity);
            LogUtil.i("count = " + count);
            for (int i = 0; i < count; ++i) {
                SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(activity,i);
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

    public static TotalConversationUnreadMessageModel getC2CTotalUnreadMessageModel(Activity activity) {
        TotalConversationUnreadMessageModel totalUnreadMessageModel = new TotalConversationUnreadMessageModel();

        UserModel user = UserModelDao.query();
        if (user == null) {
            return totalUnreadMessageModel;
        }

        long totalUnreadNum = 0;
        long cnt = SocketIOManager.getInstance().getConversationCount(activity);
        for (int i = 0; i < cnt; ++i) {
            SocketIOConversation conversation = SocketIOManager.getInstance().getConversationByIndex(activity,i);
            SocketIOConversationType type = conversation.getType();
            if (type == SocketIOConversationType.C2C) {
                // 自己对自己发的消息过滤
                if (!conversation.getPeer().equals(user.getUser_id())) {
                    long unreadnum = conversation.getUnreadMessageNum();
                    if (unreadnum > 0) {
                        List<SocketIOMessage> list = conversation.getLastMsgs(1);
                        if (list != null && list.size() > 0) {
                            SocketIOMessage msg = list.get(0);
                            MsgModel msgModel = new LiveMsgModel(msg);
                            if (msgModel.isPrivateMsg()) {
                                ConversationUnreadMessageModel unreadMessageModel = new ConversationUnreadMessageModel();
                                unreadMessageModel.setPeer(conversation.getPeer());
                                unreadMessageModel.setUnreadnum(unreadnum);
                                totalUnreadMessageModel.hashConver.put(conversation.getPeer(), unreadMessageModel);

                                totalUnreadNum = totalUnreadNum + unreadnum;
                            }
                        }
                    }
                }
            }
        }

        totalUnreadMessageModel.setTotalUnreadNum(totalUnreadNum);
        return totalUnreadMessageModel;
    }


    public  static SocketIOConversation getConversationGroup(String id) {
        SocketIOConversation conversation = null;
        if (!TextUtils.isEmpty(id)) {
            conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.Group, id);
        }
        return conversation;
    }
    public static void sendMsgC2C(final String id,final CustomMsg customMsg,final SocketIOValueCallBack<SocketIOMessage> callback) {
        if (TextUtils.isEmpty(id)) {
            callback.onError(0,"無聊天對象");
        }
        else if (!isConnected) {
            SDToast.showToast("聊天室異常");
            callback.onError(0,"聊天室異常");
        }
        else{
            CommonInterface.requestIs_black(customMsg.getSender().getUser_id(), new AppRequestCallback<User_is_blackActModel>() {
                protected void onSuccess(SDResponse resp) {
                    if (actModel.isOk()) {
                        SocketIOMessage sMsg = customMsg.parsetoSocketIOMessage();
                        sMsg.setPeer(customMsg.getSender().getUser_id());
                        mSocket.emit("c2c_msg",id,customMsg.parsetoSocketIOMessage().getJson());

                        //LogUtil.i("requestIs_black :" + customMsg.parsetoSocketIOMessage().getJson());

                        callback.onSuccess(customMsg.parsetoSocketIOMessage());

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

}
