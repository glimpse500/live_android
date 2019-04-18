package com.oolive.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.library.utils.ViewHolder;
import com.oolive.live.R;
import com.oolive.live.fragment.LiveUserHomeLeftFragment;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-12 上午10:15:04 类说明
 */
public class LiveUserHomeLeftAdapter extends SDSimpleAdapter<LiveUserHomeLeftFragment.ItemUserModel> {

    public LiveUserHomeLeftAdapter(List<LiveUserHomeLeftFragment.ItemUserModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return R.layout.item_frag_user_home;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent, LiveUserHomeLeftFragment.ItemUserModel model) {
        TextView tv_key = ViewHolder.get(R.id.tv_key, convertView);
        TextView tv_value = ViewHolder.get(R.id.tv_value, convertView);
        View view_stroke = ViewHolder.get(R.id.view_stroke, convertView);
        SDViewBinder.setTextView(tv_key, model.getKey());
        SDViewBinder.setTextView(tv_value, model.getValue());

        if (position == (getData().size() - 1)) {
            SDViewUtil.setGone(view_stroke);
        } else {
            SDViewUtil.setVisible(view_stroke);
        }
    }

}
