package com.oolive.live.appview.main;

import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.oolive.hybrid.dao.InitActModelDao;
import com.oolive.hybrid.event.ERetryInitSuccess;
import com.oolive.hybrid.model.InitActModel;
import com.fanwe.lib.viewpager.SDViewPager;
import com.fanwe.lib.viewpager.indicator.IPagerIndicatorItem;
import com.fanwe.lib.viewpager.indicator.adapter.PagerIndicatorAdapter;
import com.fanwe.lib.viewpager.indicator.impl.PagerIndicator;
import com.oolive.library.adapter.SDPagerAdapter;
import com.oolive.library.utils.SDCollectionUtil;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;
import com.oolive.live.activity.LiveChatC2CActivity;
import com.oolive.live.activity.LiveSearchUserActivity;
import com.oolive.live.appview.BaseAppView;
import com.oolive.live.appview.pagerindicator.LiveHomeTitleTab;
import com.oolive.live.appview.title.LiveMainHomeTitleView;
import com.oolive.live.common.AppRuntimeWorker;
import com.oolive.live.dialog.LiveSelectLiveDialog;
import com.oolive.live.event.EReSelectTabLiveBottom;
import com.oolive.live.event.ESelectLiveFinish;
import com.oolive.live.model.HomeTabTitleModel;
import com.oolive.live.model.LiveFilterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-主页view
 */
public class LiveMainHomeView extends BaseAppView {
    public LiveMainHomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveMainHomeView(Context context) {
        super(context);
        init();
    }

    private LiveMainHomeTitleView view_title;

    private SDViewPager vpg_content;
    private PagerIndicator view_pager_indicator;

    private List<HomeTabTitleModel> mListModel = new ArrayList<>();

    private SparseArray<LiveTabBaseView> mArrContentView = new SparseArray<>();

    private HomeTabTitleModel mSelectTitleModel;

    protected void init() {
        setContentView(R.layout.view_live_main_home);
        view_title = (LiveMainHomeTitleView) findViewById(R.id.view_title);

        vpg_content = (SDViewPager) findViewById(R.id.vpg_content);
        view_pager_indicator = (PagerIndicator) findViewById(R.id.view_pager_indicator);

        vpg_content.addOnPageChangeListener(mOnPageChangeListener);

        initTitle();
        initTabsData();
        initViewPagerIndicator();
        initViewPager();
    }

    private void initTitle() {
        view_title.setCallback(new LiveMainHomeTitleView.Callback() {
            @Override
            public void onClickSearch(View v) {
                clickSearch();
            }

            @Override
            public void onClickSelectLive(View v) {
                LiveSelectLiveDialog dialog = new LiveSelectLiveDialog(getActivity());
                dialog.show();
            }

            @Override
            public void onClickNewMsg(View v) {
                clickChatList();
            }
        });
    }

    public void onEventMainThread(ERetryInitSuccess event) {
        initTabsData();
        mPagerAdapter.notifyDataSetChanged();
        dealLastSelected();
    }

    private void dealLastSelected() {
        int index = mListModel.indexOf(mSelectTitleModel);
        if (index < 0) {
            index = 1;
        }

        vpg_content.setCurrentItem(index);
    }

    private void initTabsData() {
        mListModel.clear();

        HomeTabTitleModel tabFollow = new HomeTabTitleModel();
        tabFollow.setTitle("关注");

        HomeTabTitleModel tabHot = new HomeTabTitleModel();
        LiveFilterModel model = LiveFilterModel.get();
        String city = model.getCity();
        tabHot.setTitle(city);

        HomeTabTitleModel tabNearby = new HomeTabTitleModel();
        tabNearby.setTitle("附近");

        HomeTabTitleModel tabClub = new HomeTabTitleModel();
        tabClub.setTitle(AppRuntimeWorker.getSociatyNmae());

        mListModel.add(tabFollow);
        mListModel.add(tabHot);
        //mListModel.add(tabNearby);
        if (AppRuntimeWorker.getOpen_sociaty_module() == 1
                && !TextUtils.isEmpty(AppRuntimeWorker.getSociatyNmae())) {
            mListModel.add(tabClub);
        }

        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null) {
            List<HomeTabTitleModel> listTab = initActModel.getVideo_classified();
            if (listTab != null && !listTab.isEmpty()) {

                mListModel.addAll(listTab);
            }
        }
    }

    private void initViewPager() {
        vpg_content.setOffscreenPageLimit(2);
        vpg_content.setAdapter(mPagerAdapter);

        vpg_content.setCurrentItem(1);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mSelectTitleModel = mListModel.get(position);
            if (position == 1) {
                SDViewUtil.setVisible(view_title.getViewSelectLive());
            } else {
                SDViewUtil.setInvisible(view_title.getViewSelectLive());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private SDPagerAdapter mPagerAdapter = new SDPagerAdapter<HomeTabTitleModel>(mListModel, getActivity()) {
        @Override
        public View getView(ViewGroup container, int position) {
            LiveTabBaseView view = null;
            switch (position) {
                case 0:
                    view = new LiveTabFollowView(getActivity());
                    break;
                case 1:
                    view = new LiveTabHotView(getActivity());
                    break;
                case 2:
                    view = new LiveTabNearbyView(getActivity());
                    break;
//                case 3:
//                    if (AppRuntimeWorker.getOpen_sociaty_module() == 1 && !TextUtils.isEmpty(AppRuntimeWorker.getSociatyNmae()))
//                    {
//                        view = new LiveTabClubView(getActivity());
//                    } else
//                    {
//                        LiveTabCategoryView tabView = new LiveTabCategoryView(getActivity());
//                        tabView.setTabTitleModel(mListModel.get(position));
//                        view = tabView;
//                    }
//                    break;
                default:
                    HomeTabTitleModel titleModel = mListModel.get(position);
                    //如果是网站数据则
                    if (titleModel.getLink_url() != null && titleModel.getLink_url().startsWith("http")) {

                        LiveTabWebView tabWebView = new LiveTabWebView(getActivity());
                        tabWebView.setUrl(mListModel.get(position).getLink_url());
                        view = tabWebView;
                        break;
                    }
                    LiveTabCategoryView tabView = new LiveTabCategoryView(getActivity());
                    tabView.setTabTitleModel(mListModel.get(position));
                    view = tabView;

                    break;
            }

            if (view != null) {
                mArrContentView.put(position, view);
                view.setParentViewPager(vpg_content);
            }

            return view;
        }
    };

    private void initViewPagerIndicator() {
        view_pager_indicator.setViewPager(vpg_content);
        view_pager_indicator.setAdapter(new PagerIndicatorAdapter() {
            @Override
            protected IPagerIndicatorItem onCreatePagerIndicatorItem(int position, ViewGroup viewParent) {
                LiveHomeTitleTab item = new LiveHomeTitleTab(getActivity());
                HomeTabTitleModel model = SDCollectionUtil.get(mListModel, position);
                item.setData(model);
                return item;
            }
        });
    }

    public void onEventMainThread(ESelectLiveFinish event) {
        String text = event.model.getCity();

        IPagerIndicatorItem item = view_pager_indicator.getPagerIndicatorItem(1);
        if (item != null) {
            LiveHomeTitleTab tab = (LiveHomeTitleTab) item;
            HomeTabTitleModel model = tab.getData();
            model.setTitle(text);
            tab.setData(model);
        }
    }

    public void onEventMainThread(EReSelectTabLiveBottom event) {
        if (event.index == 0) {
            int index = vpg_content.getCurrentItem();
            LiveTabBaseView view = mArrContentView.get(index);
            if (view != null) {
                view.scrollToTop();
            }
        }
    }

    /**
     * 私聊列表
     */
    private void clickChatList() {
        Intent intent = new Intent(getActivity(), LiveChatC2CActivity.class);
        getActivity().startActivity(intent);
    }

    /**
     * 搜索
     */
    private void clickSearch() {
        Intent intent = new Intent(getActivity(), LiveSearchUserActivity.class);
        getActivity().startActivity(intent);
    }
}
