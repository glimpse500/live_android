package com.oolive.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.oolive.hybrid.activity.BaseTitleActivity;
import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.model.InitActModel;
import com.oolive.library.title.SDTitleItem;
import com.oolive.live.R;
import com.oolive.live.appview.ranking.LiveContTotalView;
import com.oolive.live.common.AppRuntimeWorker;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-12 下午8:33:19 类说明
 */
public class LiveMySelfContActivity extends BaseTitleActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    @ViewInject(R.id.fl_content)
    private FrameLayout fl_content;

    private LiveContTotalView view = null;

    private String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myself_cont);
        x.view().inject(this);
        init();
    }

    private void init() {
        initTitle();
        user_id = getIntent().getStringExtra(EXTRA_USER_ID);
        fl_content.addView(getLiveContTotalView());
    }

    private void initTitle() {
        mTitle.setMiddleTextTop(AppRuntimeWorker.getTicketName() + "贡献榜");

        if (isOpenRankingList()) {
            mTitle.initRightItem(1);
            mTitle.getItemRight(0).setTextTop("总榜");
        }

    }

    private LiveContTotalView getLiveContTotalView() {
        if (null == view) {
            view = new LiveContTotalView(getActivity());
        }
        view.setUser_id(user_id);
        view.setTipName("消费");
        view.requestCont(false);
        return view;
    }

//    private void addFragment()
//    {
//        /**
//         * 总贡献排行
//         */
//        String user_id = getIntent().getStringExtra(EXTRA_USER_ID);
//        Bundle bundle = new Bundle();
//        bundle.putString(EXTRA_USER_ID, user_id);
//        getSDFragmentManager().toggle(R.id.ll_content, null, LiveContTotalView.class, bundle);
//    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {
        if (isOpenRankingList()) {
            Intent intent = new Intent(LiveMySelfContActivity.this, LiveRankingActivity.class);
            startActivity(intent);
        }
    }

    private boolean isOpenRankingList() {
        InitActModel model = InitActModelDao.query();
        if (model != null) {
            if (model.getOpen_ranking_list() == 1) {
                return true;
            }
        }
        return false;
    }
}
