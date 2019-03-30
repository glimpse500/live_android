package com.oolive.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.ViewHolder;
import com.oolive.live.R;
import com.oolive.live.model.UserModel;
import com.oolive.live.utils.GlideUtil;

import java.util.List;

/**
 * Created by HSH on 2016/7/15.
 */

public class LiveBlacklistAdapter extends SDSimpleAdapter<UserModel> {
    public LiveBlacklistAdapter(List<UserModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_live_blacklist;
    }

    @Override
    public void bindData(final int position, View convertView, ViewGroup parent, final UserModel model) {
        TextView tv_nick_name = ViewHolder.get(R.id.tv_nick_name, convertView);
        TextView tv_signature = ViewHolder.get(R.id.tv_signature, convertView);
        ImageView iv_head_image = ViewHolder.get(R.id.iv_head_image, convertView);
        ImageView iv_sex = ViewHolder.get(R.id.iv_sex, convertView);
        ImageView iv_rank = ViewHolder.get(R.id.iv_rank, convertView);

        SDViewBinder.setTextView(tv_nick_name, model.getNick_name());
        SDViewBinder.setTextView(tv_signature, model.getSignature());
        GlideUtil.loadHeadImage(model.getHead_image()).into(iv_head_image);
        iv_sex.setImageResource(model.getSexResId());
        iv_rank.setImageResource(model.getLevelImageResId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemClickCallback(position, model, v);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notifyItemLongClickCallback(position, model, v);
                return false;
            }
        });

    }
}
