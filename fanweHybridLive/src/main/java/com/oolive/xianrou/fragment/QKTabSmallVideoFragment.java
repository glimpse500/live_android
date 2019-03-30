package com.oolive.xianrou.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oolive.xianrou.appview.main.QKTabSmallVideoView;
import com.oolive.xianrou.fragment.base.XRBaseFragment;

/**
 * 小视频列表
 * Created by LianCP on 2017/7/19.
 */
public class QKTabSmallVideoFragment extends XRBaseFragment {

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new QKTabSmallVideoView(container.getContext());
    }
}