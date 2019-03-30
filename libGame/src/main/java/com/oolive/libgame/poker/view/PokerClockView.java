package com.oolive.libgame.poker.view;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fanwe.games.R;
import com.oolive.libgame.view.BaseGameView;

/**
 * 开牌倒计时view
 */
public class PokerClockView extends BaseGameView
{
    public PokerClockView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public PokerClockView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PokerClockView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView tv_time;

    private void init()
    {
        setContentView(R.layout.view_poker_clock);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    public void setTextLeftTime(String text)
    {
        tv_time.setText(text);
    }

}
