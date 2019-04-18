package com.oolive.hybrid.activity;

import android.view.View;

import com.oolive.library.title.SDTitleItem;
import com.oolive.library.title.SDTitleSimple;
import com.oolive.library.title.SDTitleSimple.SDTitleSimpleListener;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;

public class BaseTitleActivity extends BaseActivity implements SDTitleSimpleListener {
    protected SDTitleSimple mTitle;

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        mTitle = (SDTitleSimple) findViewById(R.id.title);
        mTitle.setLeftImageLeft(R.drawable.ic_arrow_left_main_color);
        mTitle.setmListener(this);
    }

    @Override
    protected int onCreateTitleViewResId() {
        return R.layout.include_title_simple;
    }

    @Override
    public void onCLickLeft_SDTitleSimple(SDTitleItem v) {
        finish();
    }

    @Override
    public void onCLickMiddle_SDTitleSimple(SDTitleItem v) {

    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index) {

    }

    protected void isShowTitle(boolean isShowTitle) {
        if (isShowTitle) {
            SDViewUtil.setVisible(mTitle);
        } else {
            SDViewUtil.setGone(mTitle);
        }
    }
}
