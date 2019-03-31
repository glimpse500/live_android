package com.oolive.live.activity;

import android.os.Bundle;

import com.oolive.hybrid.activity.BaseActivity;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDKeyboardListener;
import com.oolive.live.R;
import com.oolive.live.appview.LivePrivateChatView;
import com.oolive.live.appview.LivePrivateChatView.ClickListener;

public class LivePrivateChatActivity extends BaseActivity {
    /**
     * 聊天对象user_id(String)
     */
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_CHAT_ID = "extra_chat_id";
    private LivePrivateChatView view_private_chat;

    private SDKeyboardListener mKeyboardListener = new SDKeyboardListener();

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setContentView(R.layout.act_live_private_chat);
        view_private_chat = (LivePrivateChatView) findViewById(R.id.view_private_chat);

        String user_id = getIntent().getStringExtra(EXTRA_USER_ID);
        LogUtil.i("Peer ID = " + user_id);
        //String chat_id = getIntent().getStringExtra(EXTRA_CHAT_ID);
        //LogUtil.i("Chat Peer ID = " + chat_id);
        view_private_chat.setLockHeightEnable(true);
        view_private_chat.setClickListener(new ClickListener() {

            @Override
            public void onClickBack() {
                finish();
            }
        });
        view_private_chat.setUserId(user_id);
        mKeyboardListener.setActivity(this).setKeyboardVisibilityCallback(new SDKeyboardListener.SDKeyboardVisibilityCallback() {
            @Override
            public void onKeyboardVisibilityChange(boolean visible, int height) {
                view_private_chat.onKeyboardVisibilityChange(visible, height);
            }
        });
    }
}
