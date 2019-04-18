package com.oolive.o2o.fragment;

import android.content.Intent;
import android.widget.LinearLayout;

import com.oolive.hybrid.fragment.BaseFragment;
import com.oolive.live.R;
import com.oolive.live.activity.LivePrivateChatActivity;
import com.oolive.live.appview.LiveChatC2CNewView;
import com.oolive.live.model.LiveConversationListModel;

import org.xutils.view.annotation.ViewInject;

/**
 * Created by Administrator on 2016/10/31.
 */

public class O2OShoppingLiveImFragment extends BaseFragment {
    @ViewInject(R.id.ll_content)
    private LinearLayout ll_content;

    @Override
    protected int onCreateContentView() {
        return R.layout.act_live_chat_c2c;
    }

    @Override
    protected void init() {
        super.init();
        LiveChatC2CNewView view = new LiveChatC2CNewView(getActivity());
        view.hideRl_back();
        view.setClickListener(new LiveChatC2CNewView.ClickListener() {

            @Override
            public void onClickBack() {
                getActivity().finish();
            }
        });

        view.setOnChatItemClickListener(new LiveChatC2CNewView.OnChatItemClickListener() {
            @Override
            public void onChatItemClickListener(LiveConversationListModel itemLiveChatListModel) {
                Intent intent = new Intent(getActivity(), LivePrivateChatActivity.class);
                intent.putExtra(LivePrivateChatActivity.EXTRA_USER_ID, itemLiveChatListModel.getPeer());
                intent.putExtra(LivePrivateChatActivity.EXTRA_CHAT_ID, itemLiveChatListModel.getPeerChatID());
                startActivity(intent);
            }
        });
        ll_content.addView(view);
        //传入数据
        view.requestData();
    }
}
