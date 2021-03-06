package com.oolive.libgame.poker.goldflower.view;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.oolive.libgame.poker.goldflower.GoldFlowerUtil;
import com.oolive.libgame.poker.model.PokerGroupResultData;
import com.oolive.libgame.poker.view.PokerFlyView;
import com.oolive.libgame.poker.view.PokerGameView;
import com.oolive.library.utils.SDCollectionUtil;
import com.fanwe.games.R;

import java.util.List;

/**
 * 炸金花游戏view
 */
public class GoldFlowerGameView extends PokerGameView
{
    public GoldFlowerGameView(@NonNull Context context)
    {
        super(context);
        init();
    }

    public GoldFlowerGameView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public GoldFlowerGameView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setContentView(R.layout.view_goldflower_game);

        view_poker_bet_0.setImageStar(R.drawable.ic_goldflower_star_0);
        view_poker_bet_1.setImageStar(R.drawable.ic_goldflower_star_1);
        view_poker_bet_2.setImageStar(R.drawable.ic_goldflower_star_2);
    }

    @Override
    protected void initPokerFlyView(PokerFlyView pokerFlyView)
    {
        super.initPokerFlyView(pokerFlyView);

        pokerFlyView.setImagePoker(R.drawable.bg_poker_back_goldflower);
        pokerFlyView.setImagePokers(R.drawable.ic_pokers_goldflower);

        pokerFlyView.addTarget(view_poker_group_0.getPokerView(0));
        pokerFlyView.addTarget(view_poker_group_0.getPokerView(1));
        pokerFlyView.addTarget(view_poker_group_0.getPokerView(2));

        pokerFlyView.addTarget(view_poker_group_1.getPokerView(0));
        pokerFlyView.addTarget(view_poker_group_1.getPokerView(1));
        pokerFlyView.addTarget(view_poker_group_1.getPokerView(2));

        pokerFlyView.addTarget(view_poker_group_2.getPokerView(0));
        pokerFlyView.addTarget(view_poker_group_2.getPokerView(1));
        pokerFlyView.addTarget(view_poker_group_2.getPokerView(2));
    }

    @Override
    public void onResultData(List<PokerGroupResultData> listData)
    {
        super.onResultData(listData);

        PokerGroupResultData data0 = SDCollectionUtil.get(listData, 0);
        if (data0 != null)
        {
            view_poker_result_0.setImageResult(GoldFlowerUtil.getResultTypeImageResId(data0.getResultType()));
        }

        PokerGroupResultData data1 = SDCollectionUtil.get(listData, 1);
        if (data1 != null)
        {
            view_poker_result_1.setImageResult(GoldFlowerUtil.getResultTypeImageResId(data1.getResultType()));
        }

        PokerGroupResultData data2 = SDCollectionUtil.get(listData, 2);
        if (data2 != null)
        {
            view_poker_result_2.setImageResult(GoldFlowerUtil.getResultTypeImageResId(data2.getResultType()));
        }
    }
}
