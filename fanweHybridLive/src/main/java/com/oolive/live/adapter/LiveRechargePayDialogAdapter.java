package com.oolive.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.SDResourcesUtil;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.library.utils.ViewHolder;
import com.oolive.live.R;
import com.oolive.live.model.PayItemModel;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-7 上午11:58:06 类说明
 */
public class LiveRechargePayDialogAdapter extends SDSimpleAdapter<PayItemModel> {
    public LiveRechargePayDialogAdapter(List<PayItemModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_live_recharge_pay_dialog;
    }

    @Override
    public void bindData(final int position, final View convertView, final ViewGroup parent, final PayItemModel model) {
        final TextView tv_name = ViewHolder.get(R.id.tv_name, convertView);
        SDViewBinder.setTextView(tv_name, model.getName());
        if (model.isSelected()) {
            tv_name.setTextColor(SDResourcesUtil.getColor(R.color.white));
            SDViewUtil.setBackgroundResource(tv_name, R.drawable.layer_main_color_corner_12dp);
        } else {
            tv_name.setTextColor(SDResourcesUtil.getColor(R.color.res_main_color));
            SDViewUtil.setBackgroundResource(tv_name, R.drawable.layer_white_stroke_main_color_corner_12dp);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemClickCallback(position, model, convertView);
            }
        });
    }

}
