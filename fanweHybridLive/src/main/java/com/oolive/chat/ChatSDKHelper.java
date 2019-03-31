package com.oolive.chat;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.text.TextUtils;

import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.hybrid.constant.ApkConstant;
import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.model.InitActModel;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDToast;
import com.oolive.live.business.LiveMsgBusiness;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.event.EImOnNewMessages;
import com.oolive.live.event.ERefreshMsgUnReaded;
import com.oolive.live.model.ConversationUnreadMessageModel;
import com.oolive.live.model.TotalConversationUnreadMessageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.model.User_is_blackActModel;
import com.oolive.live.model.custommsg.CustomMsg;
import com.oolive.live.model.custommsg.LiveMsgModel;
import com.oolive.live.model.custommsg.MsgModel;
import com.oolive.socketio.SocketIOConversation;
import com.oolive.socketio.SocketIOConversationType;
import com.oolive.socketio.SocketIOHelper;
import com.oolive.socketio.SocketIOManager;
import com.oolive.socketio.SocketIOMessage;
import com.oolive.socketio.SocketIOValueCallBack;
import com.sunday.eventbus.SDEventManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.DaoCore;
import co.chatsdk.core.dao.Message;
import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.error.ChatSDKException;
import co.chatsdk.core.events.EventType;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.hook.Hook;
import co.chatsdk.core.hook.HookEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatSDKHelper {
    private static Socket mSocket;
    private static boolean isInLogin = false;
    private static Context appContext = null;
    private static Boolean isConnected = false;
    private static String roomID;
    private static User chatSdkUser_self = null;
    private static ChatSDK chatSDK;
    private static String userID;
    private static String chat_id;
    private static String peer;
    private static final String password = "oolive_pwd";
    private static ProgressDialog progressDialog;
    private static boolean register = false;
    private static Disposable  msgListener = null;
    public static void init(Context context){
        LogUtil.e("Initial Chat SDK");
        if (appContext == null)
            appContext = context;
        try {
            Configuration.Builder builder = new Configuration.Builder(context);
            builder.firebaseRootPath("live_chat");
            ChatSDK.initialize(builder.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));

        } catch (ChatSDKException e) {
            e.printStackTrace();
        }
        FirebaseFileStorageModule.activate();
    }
    private static void listenMsg(Activity activity){
        LogUtil.i("監聽訊息開始");

        msgListener = ChatSDK.events().sourceOnMain()
                .filter(NetworkEvent.filterType(EventType.MessageAdded))
                .subscribe(new Consumer<NetworkEvent>() {
                    @Override
                    public void accept(NetworkEvent networkEvent) throws Exception {
                        networkEvent.message.setRead(true);
                        networkEvent.message.update();
                        String json_message = networkEvent.message.getText();
                        LogUtil.i("on json_message  :" + networkEvent.message.getText());
                        try {
                            JSONObject message_body = new JSONObject(json_message);
                            CustomMsg cMsg = LiveMsgBusiness.json2CustomMsg(json_message, Integer.valueOf(message_body.getString("type")));
                            SocketIOMessage sMsg = cMsg.parsetoSocketIOMessage();
                            MsgModel msg = new LiveMsgModel(sMsg);
                            if (msg != null) {
                                msg.setCustomMsg(cMsg);
                                LogUtil.i("cMsg.getSender().getUser_id() + " +  cMsg.getSender().getUser_id());
                                msg.setConversationPeer(cMsg.getSender().getUser_id());
                                EImOnNewMessages event = new EImOnNewMessages();
                                SocketIOConversation conversation = SocketIOManager.getInstance().getConversation(SocketIOConversationType.C2C, cMsg.getSender().getUser_id());
                                conversation.writeLocalMessage(sMsg, activity,false);
                                event.msg = msg;
                                LogUtil.i("cMsg.getConversationPeer() + " +  msg.getConversationPeer());
                                SDEventManager.post(event);
                                ChatMsgStore.postERefreshMsgUnReaded(false);
                            }
                        } catch (JSONException e) {
                            //Log.e(TAG, e.getMessage());
                            return;
                        }
                    }
                });
        ChatMsgStore.init(activity);
    }
    public static void loginChatSDK(String raw_userID, String userSig, Activity activity) {

        if (isInLogin) {
            return;
        }
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel == null) {
            LogUtil.e("login  error because of null InitActModel");
            return;
        }
       userID = raw_userID;
        Action doFinally = new Action() {
            public void run() throws Exception {
                LogUtil.i("doFinally" );
                dismissProgressDialog();
            }
        };
        showProgressDialog("登入聊天伺服器中",activity);
        if (ChatSDK.auth().isAuthenticatedThisSession() && getChatID() != null && getChatID() .equals(ChatSDK.currentUserID())) {
            isConnected = true;
            isInLogin = true;
            LogUtil.i("已經登入!!  (cache_1) ");
            dismissProgressDialog();
        } else if (ChatSDK.auth().isAuthenticated()) {
            ChatSDK.auth().authenticate().subscribe(() ->
            {
                LogUtil.i("cache2 " + ChatSDK.currentUser().getEntityID());
                if (getChatID() == null || getChatID().equals(ChatSDK.currentUserID())) {
                    LogUtil.i("登入成功!!  (cache_2) " + ChatSDK.currentUserID());
                    SDToast.showToast("登入成功!!  (cache_2) " + ChatSDK.currentUserID());
                    isInLogin = true;
                    isConnected = true;
                    dismissProgressDialog();
                    listenMsg(activity);
                }
                else{
                    registe_login(raw_userID,activity);
                }
            }, throwable -> {
                isInLogin = false;
                registe_login(raw_userID,activity);
            });
        } else {
            registe_login(raw_userID,activity);
            isInLogin = false;
        }
    }

    private static void registe_login(String raw_userID,Activity activity){
        String username = "user_" + raw_userID + "@oolive.com";
        AccountDetails details = new AccountDetails();
        details.type = AccountDetails.Type.Username;
        details.username = username;
        details.password = password;
        showProgressDialog("驗證帳號中",activity);
        Disposable d = ChatSDK.auth().authenticate(details).subscribe(() -> {
                    LogUtil.i("登入成功!! (帳號) "+ ChatSDK.currentUser().getEntityID() );
                    SDToast.showToast("登入成功!! (帳號)"+ ChatSDK.currentUser().getEntityID());
                    CommonInterface.updateChatID(null,ChatSDK.currentUser().getEntityID());
                    isInLogin = true;
                    isConnected = true;
                    register = true;
                    dismissProgressDialog();
                    listenMsg(activity);
                }, throwable -> {
                    details.type = AccountDetails.Type.Register;
                    ChatSDK.auth().authenticate(details).subscribe(() -> {
                        LogUtil.i("註冊聊天帳號成功 "+ ChatSDK.currentUserID() );
                        SDToast.showToast("註冊聊天帳號成功" + ChatSDK.currentUserID());
                        isInLogin = true;
                        isConnected = true;
                        register = true;
                        dismissProgressDialog();
                    }, throwable2 -> {
                        LogUtil.i("註冊聊天帳號失敗 " +throwable2.toString());
                        register=false;
                        dismissProgressDialog();
                        listenMsg(activity);
                    });
            });
    }
    public static void  setCurrentPeer(String currentPeer){
        peer  = currentPeer;
    }
    public static String getUserID(){
        return userID;
    }
    public static boolean isConnected(){
        return isConnected;
    }
    protected static void showProgressDialog( String message,Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }
        LogUtil.i("progressDialog.show() in;" +  message);
        if (!progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(message);
            progressDialog.show();
            LogUtil.i("progressDialog.show();" );
        }
        else{
            progressDialog.setMessage(message);
            LogUtil.i("setMessage.show();" + message );
        }
    }
    protected static void dismissProgressDialog() {
        // For handling orientation changed.
        LogUtil.i("dismissProgressDialog() in;" );
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                LogUtil.i("progressDialog.dismiss();" );
            }
        } catch (Exception e) {
            ChatSDK.logError(e);
        }
    }
    public static void logout(){
        isInLogin = false;
        ChatSDKHelper.setChatID(null);
        ChatSDK.auth().logout();
    }
    public static void  setChatID(String ChatID){
        chat_id = ChatID;
    }
    public static String getChatID(){
        return chat_id;
    }

    public static void sendMsgC2C(final String id, final Thread c2cThread, final CustomMsg customMsg, final SocketIOValueCallBack<SocketIOMessage> callback) {
        if (TextUtils.isEmpty(id)) {
            callback.onError(0,"無聊天對象");
        }
        else{
            CommonInterface.requestIs_black(customMsg.getSender().getUser_id(), new AppRequestCallback<User_is_blackActModel>() {
                protected void onSuccess(SDResponse resp) {
                    if (actModel.isOk()) {
                        String json = customMsg.parsetoSocketIOMessage().getJson();
                        ChatSDK.thread().sendMessageWithText(json, c2cThread).subscribe(messageSendProgress -> {
                            //LogUtil.i("customMsg.parsetoSocketIOMessage().getJson() =  " + json);
                            callback.onSuccess(customMsg.parsetoSocketIOMessage());
                        }, throwable -> {
                            callback.onError(1,"無法傳送訊息");
                        });
                    }
                    else {
                        callback.onError(1,"無法傳送訊息");
                    }
                }
            });
        }

    }

}