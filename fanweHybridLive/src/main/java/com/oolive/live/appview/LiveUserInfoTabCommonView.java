package com.oolive.live.appview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oolive.library.utils.SDViewBinder;
import com.oolive.live.R;
import com.oolive.live.activity.LiveFollowActivity;
import com.oolive.live.activity.LiveMainActivity;
import com.oolive.live.activity.LiveMyFocusActivity;
import com.oolive.live.activity.LiveUserHomeReplayActivity;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.UserModel;
import com.oolive.live.utils.LiveUtils;
import com.oolive.xianrou.activity.QKMySmallVideoActivity;

/**
 * Created by yhz on 2017/9/1.
 */

public class LiveUserInfoTabCommonView extends BaseAppView {
    private LinearLayout ll_small_video;
    private TextView tv_small_video_num;
    private LinearLayout ll_video;
    private TextView tv_video_num;
    private LinearLayout ll_my_focus;
    private TextView tv_focus_count;
    private LinearLayout ll_my_fans;
    private TextView tv_fans_count;

    private UserModel mUser = null; // 不一定是使用者的id

    public LiveUserInfoTabCommonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LiveUserInfoTabCommonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveUserInfoTabCommonView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setContentView(R.layout.view_user_info_tab);
        initView();
        initListerner();
    }

    private void initView() {
        ll_small_video = (LinearLayout) findViewById(R.id.ll_small_video);
        tv_small_video_num = (TextView) findViewById(R.id.tv_small_video_num);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        tv_video_num = (TextView) findViewById(R.id.tv_video_num);
        ll_my_focus = (LinearLayout) findViewById(R.id.ll_my_focus);
        tv_focus_count = (TextView) findViewById(R.id.tv_focus_count);
        ll_my_fans = (LinearLayout) findViewById(R.id.ll_my_fans);
        tv_fans_count = (TextView) findViewById(R.id.tv_fans_count);

        //To Do
        ll_small_video.setVisibility(GONE);
        tv_small_video_num.setVisibility(GONE);
        ll_video.setVisibility(GONE);
        tv_video_num.setVisibility(GONE);
    }

    private void initListerner() {
        ll_small_video.setOnClickListener(this);
        ll_my_focus.setOnClickListener(this);
        ll_my_fans.setOnClickListener(this);
        ll_video.setOnClickListener(this);
    }

    public void setData(UserModel user) {
        if (user == null) {
            return;
        }
        mUser = user;

        SDViewBinder.setTextView(tv_small_video_num, String.valueOf(user.getN_svideo_count()));

        SDViewBinder.setTextView(tv_video_num, String.valueOf(user.getVideo_count()));

        SDViewBinder.setTextView(tv_focus_count, String.valueOf(user.getFocus_count()));

        SDViewBinder.setTextView(tv_fans_count, LiveUtils.getFormatNumber(user.getFans_count()));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ll_small_video) {
            //clickLlSmallVideo();
        } else if (v == ll_my_focus) {
            clickLlMyFocus();
        } else if (v == ll_my_fans) {
            clickLlMyFans();
        } else if (v == ll_video) {
            //clickRlVideo();
        }
    }

    /**
     * 小视频
     */
    private void clickLlSmallVideo() {
        Intent intent = new Intent(getActivity(), QKMySmallVideoActivity.class);
        getActivity().startActivity(intent);
    }

    // 我关注的人
    protected void clickLlMyFocus() {
        //UserModel user = UserModelDao.query();
        UserModel user = mUser;

        if (user == null) {
            return;
        }
        String user_id = user.getUser_id();
        Intent intent = new Intent(getActivity(), LiveFollowActivity.class);
        intent.putExtra(LiveFollowActivity.EXTRA_USER_ID, user_id);
        Bundle bundle=new Bundle();
        bundle.putSerializable(LiveFollowActivity.EXTRA_USER, mUser);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, LiveMainActivity.RETURN_FROM_FOCUS_LIST);

    }

    // 我的粉丝
    protected void clickLlMyFans() {
//        Intent intent = new Intent(getActivity(), LiveMyFocusActivity.class);
//        intent.putExtra(LiveFollowActivity.EXTRA_USER_ID, mUser.getUser_id());
//
////        getActivity().startActivity(intent);
//        getActivity().startActivityForResult(intent, LiveMainActivity.RETURN_FROM_FOCUS_LIST);
//        UserModel user = mUser;
//
//        if (user == null) {
//            return;
//        }
        UserModel user = mUser;
        if (user == null) {
            return;
        }
        String user_id = user.getUser_id();
        Intent intent = new Intent(getActivity(), LiveMyFocusActivity.class);
        intent.putExtra(LiveMyFocusActivity.EXTRA_USER_ID, user_id);
        Bundle bundle=new Bundle();
        bundle.putSerializable(LiveMyFocusActivity.EXTRA_USER, mUser);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, LiveMainActivity.RETURN_FROM_FOCUS_LIST);


    }

    // 回放列表
    private void clickRlVideo() {
        Intent intent = new Intent(getActivity(), LiveUserHomeReplayActivity.class);
        getActivity().startActivity(intent);
    }
}
