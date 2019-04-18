package com.oolive.live.view.pulltorefresh;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.fanwe.lib.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.lib.pulltorefresh.SDPullToRefreshView;
import com.fanwe.lib.pulltorefresh.loadingview.SimpleImageLoadingView;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 下拉刷新的HeaderView或者FooterView
 */
public class AppPullToRefreshLoadingView extends SimpleImageLoadingView {
    public AppPullToRefreshLoadingView(@NonNull Context context) {
        super(context);
    }

    public AppPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppPullToRefreshLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private GifDrawable mGifDrawable;

    @Override
    protected void init() {
        super.init();
        SDViewUtil.setSize(getImageView(), SDViewUtil.dp2px(35), SDViewUtil.dp2px(40));
        getImageView().setPadding(0, SDViewUtil.dp2px(2.5f), 0, SDViewUtil.dp2px(2.5f));
    }

    private GifDrawable getGifDrawable() {
        if (mGifDrawable == null) {
            try {
                mGifDrawable = new GifDrawable(getContext().getResources(), R.drawable.ic_ptr_state_refreshing);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mGifDrawable;
    }

    private void resetGifDrawable() {
        if (mGifDrawable != null) {
            mGifDrawable.stop();
            mGifDrawable.seekToFrame(0);
        }
    }

    @Override
    public void onStateChanged(ISDPullToRefreshView.State newState, ISDPullToRefreshView.State oldState, SDPullToRefreshView view) {
        resetGifDrawable();

        switch (newState) {
            case RESET:
            case PULL_TO_REFRESH:
            case REFRESH_FINISH:
                getImageView().setImageResource(R.drawable.ic_ptr_state_normal);
                break;
            case RELEASE_TO_REFRESH:
                getImageView().setImageResource(R.drawable.ic_ptr_state_normal);
                break;
            case REFRESHING:
                getImageView().setImageDrawable(getGifDrawable());
                getGifDrawable().start();
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mGifDrawable != null) {
            mGifDrawable.recycle();
            mGifDrawable = null;
        }
    }
}
