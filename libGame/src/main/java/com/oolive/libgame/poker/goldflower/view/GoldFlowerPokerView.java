package com.oolive.libgame.poker.goldflower.view;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.oolive.libgame.poker.view.PokerView;
import com.fanwe.games.R;

/**
 * 炸金花单张扑克牌view
 */
public class GoldFlowerPokerView extends PokerView
{
    public GoldFlowerPokerView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public GoldFlowerPokerView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GoldFlowerPokerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setPokerBackImageResId(R.drawable.bg_poker_back_goldflower);
    }
}
