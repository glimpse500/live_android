package com.oolive.auction.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.auction.model.GoodsTagsModel;
import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.drawable.SDDrawable;
import com.oolive.live.R;

import java.util.List;

/**
 * Created by shibx on 2016/8/9.
 */
public class VirtualGoodsTagAdapter extends SDSimpleAdapter<GoodsTagsModel> {

    public VirtualGoodsTagAdapter(List<GoodsTagsModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_label_auction;
    }

    @Override
    public void bindData(final int position, final View convertView, ViewGroup parent, final GoodsTagsModel model) {

        TextView tv_label = get(R.id.tv_label, convertView);
        tv_label.setText(model.getName());
        tv_label.setBackgroundResource(R.drawable.selector_gray_main_color_corner_auction);
        tv_label.setTextColor(SDDrawable.getStateListColor(getActivity().getResources().getColor(R.color.res_text_gray_s), getActivity().getResources().getColor(R.color.white), getActivity().getResources().getColor(R.color.white), getActivity().getResources().getColor(R.color.white)));
        if (model.isSelected()) {
            tv_label.setSelected(true);
        } else {
            tv_label.setSelected(false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemClickCallback(position, model, v);
            }
        });
    }
}
