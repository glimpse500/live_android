package com.oolive.o2o.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oolive.hybrid.fragment.BaseFragment;
import com.oolive.live.appview.main.LiveMainHomeView;

/**
 * Created by Administrator on 2016/10/31.
 */

public class O2OShoppingLiveTabLiveFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new LiveMainHomeView(container.getContext());
    }
}
