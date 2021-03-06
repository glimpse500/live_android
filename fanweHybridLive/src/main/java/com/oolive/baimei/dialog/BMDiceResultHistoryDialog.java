package com.oolive.baimei.dialog;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oolive.baimei.adapter.BMDiceResultHistoryAdapter;
import com.oolive.baimei.model.BMDiceResultHistoryModel;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名: com.fanwe.baimei.dialog
 * 描述: 猜大小结果历史列表弹窗
 * 作者: Su
 * 创建时间: 2017/5/27 17:19
 **/
public class BMDiceResultHistoryDialog extends SDDialogBase {
    private RecyclerView mRecyclerView;
    private BMDiceResultHistoryAdapter mAdapter;


    public BMDiceResultHistoryDialog(Activity activity) {
        super(activity);

        init();
    }

    private void init() {
        setContentView(R.layout.bm_dialog_dice_result_history);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setAnimations(R.style.res_Anim_SlidingTopTop);

        paddingLeft(SDViewUtil.getScreenWidthPercent(0.15f));
        paddingRight(SDViewUtil.getScreenWidthPercent(0.15f));

        getRecyclerView().setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        getRecyclerView().setAdapter(getAdapter());
    }

    private RecyclerView getRecyclerView() {
        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_dice_result_history);
        }
        return mRecyclerView;
    }

    private BMDiceResultHistoryAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new BMDiceResultHistoryAdapter(getOwnerActivity());
        }
        return mAdapter;
    }

    public void setData(@NonNull int[] data) {
        if (data.length > 0) {
            List<BMDiceResultHistoryModel> list = new ArrayList<>();

            int n = data.length;
            for (int i = 0; i < n; i++) {
                BMDiceResultHistoryModel model = new BMDiceResultHistoryModel(i == 0, data[i]);
                list.add(model);
            }

            getAdapter().setData(list);
            getAdapter().notifyDataSetChanged();
        }

    }

}
