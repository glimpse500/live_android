package com.oolive.live.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.oolive.hybrid.fragment.BaseFragment;
import com.oolive.hybrid.http.AppRequestCallback;
import com.fanwe.lib.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.library.listener.SDItemClickCallback;
import com.oolive.library.utils.SDToast;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.library.view.SDTabUnderline;
import com.oolive.live.R;
import com.oolive.live.activity.LiveUserHomeActivity;
import com.oolive.live.adapter.LiveFamilyApplyAdapter;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.dao.UserModelDao;
import com.oolive.live.model.App_family_user_confirmActModel;
import com.oolive.live.model.App_family_user_r_user_listActModel;
import com.oolive.live.model.PageModel;
import com.oolive.live.model.UserModel;
import com.oolive.live.view.pulltorefresh.IPullToRefreshViewWrapper;
import com.oolive.live.view.pulltorefresh.PullToRefreshViewWrapper;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员申请列表
 * Created by Administrator on 2016/9/26.
 */

public class LiveFamilyApplyFragment extends BaseFragment {
    private PullToRefreshViewWrapper mPullToRefreshViewWrapper = new PullToRefreshViewWrapper();
    @ViewInject(R.id.lv_fam_members)
    private ListView lv_fam_members;

    private LiveFamilyApplyAdapter adapter;
    private List<UserModel> listModel;

    private PageModel pageModel = new PageModel();
    private int page = 1;

    private int is_agree = 1;//是否同意 （1：同意，2：拒绝）

    private SDTabUnderline tab_live_apply, tab_live_menb;
    private int apply_count;//成员申请人数

    @Override
    protected int onCreateContentView() {
        return R.layout.frag_live_family_members;
    }

    @Override
    protected void init() {
        super.init();
        initData();
    }

    private void initData() {
        listModel = new ArrayList<>();
        adapter = new LiveFamilyApplyAdapter(listModel, getActivity());
        lv_fam_members.setAdapter(adapter);
        initPullToRefresh();

        /**
         * 用户详情
         */
        adapter.setItemClickCallback(new SDItemClickCallback<UserModel>() {
            @Override
            public void onItemClick(int position, UserModel item, View view) {
                Intent intent = new Intent(getActivity(), LiveUserHomeActivity.class);
                intent.putExtra(LiveUserHomeActivity.EXTRA_USER_ID, item.getUser_id());
                intent.putExtra(LiveUserHomeActivity.EXTRA_FAMILY, LiveUserHomeActivity.EXTRA_FAMILY);
                startActivity(intent);
            }
        });

        /**
         * 同意加入
         */
        adapter.setClickAgreeListener(new SDItemClickCallback<UserModel>() {
            @Override
            public void onItemClick(int position, UserModel item, View view) {
                is_agree = 1;
                int user_id = Integer.parseInt(item.getUser_id());
                familyMemberConfirm(user_id, is_agree, item);
            }
        });

        /**
         * 拒绝加入家族
         */
        adapter.setClickRefuseListener(new SDItemClickCallback<UserModel>() {
            @Override
            public void onItemClick(int position, UserModel item, View view) {
                is_agree = 2;
                int user_id = Integer.parseInt(item.getUser_id());
                familyMemberConfirm(user_id, is_agree, item);
            }
        });
    }

    private void initPullToRefresh() {
        mPullToRefreshViewWrapper.setPullToRefreshView((SDPullToRefreshView) findViewById(R.id.view_pull_to_refresh));
        mPullToRefreshViewWrapper.setOnRefreshCallbackWrapper(new IPullToRefreshViewWrapper.OnRefreshCallbackWrapper() {
            @Override
            public void onRefreshingFromHeader() {
                refreshViewer();
            }

            @Override
            public void onRefreshingFromFooter() {
                loadMoreViewer();
            }
        });
    }

    public void refreshViewer() {
        page = 1;
        requestFamilyMembersApplyList(false);
    }

    private void loadMoreViewer() {
        if (pageModel.getHas_next() == 1) {
            page++;
            requestFamilyMembersApplyList(true);
        } else {
            SDToast.showToast("没有更多数据了");
            mPullToRefreshViewWrapper.stopRefreshing();
        }
    }

    /**
     * 获取家族成员申请列表
     *
     * @param isLoadMore
     */
    private void requestFamilyMembersApplyList(final boolean isLoadMore) {
        UserModel dao = UserModelDao.query();
        CommonInterface.requestFamilyMembersApplyList(dao.getFamily_id(), page, new AppRequestCallback<App_family_user_r_user_listActModel>() {
            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.getStatus() == 1) {
                    apply_count = actModel.getApply_count();
                    tab_live_apply.setTextTitle("成员申请(" + apply_count + ")");
                    tab_live_menb.setTextTitle("家族成员(" + actModel.getRs_count() + ")");
                    pageModel = actModel.getPage();
                    SDViewUtil.updateAdapterByList(listModel, actModel.getList(), adapter, isLoadMore);
                }
            }

            @Override
            protected void onFinish(SDResponse resp) {
                super.onFinish(resp);
                mPullToRefreshViewWrapper.stopRefreshing();
            }
        });
    }

    public void setApplyRsCount(SDTabUnderline textView, SDTabUnderline textView2) {
        this.tab_live_apply = textView;
        this.tab_live_menb = textView2;
    }

    /**
     * 成员申请审核
     *
     * @param user_id
     * @param is_agree
     */
    private void familyMemberConfirm(int user_id, int is_agree, final UserModel item) {
        CommonInterface.requestFamilyMemberConfirm(user_id, is_agree, new AppRequestCallback<App_family_user_confirmActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if (actModel.getStatus() == 1) {
                    SDToast.showToast(actModel.getError().toString());
                    adapter.removeData(item);
                    if (apply_count > 0) {
                        apply_count = apply_count - 1;
                        tab_live_apply.setTextTitle("成员申请(" + apply_count + ")");
                    }
                }
            }
        });
    }
}
