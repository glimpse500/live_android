package com.oolive.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.ViewHolder;
import com.oolive.live.R;
import com.oolive.live.model.ItemApp_user_reviewModel;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-13 下午5:34:09 类说明
 */
public class LiveUserHomeRightAdapter extends SDSimpleAdapter<ItemApp_user_reviewModel> {
    public LiveUserHomeRightAdapter(List<ItemApp_user_reviewModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_frag_user_home_right_ios;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent, ItemApp_user_reviewModel model) {
        TextView tv_title = ViewHolder.get(R.id.tv_title, convertView);
        TextView tv_begin_time_format = ViewHolder.get(R.id.tv_begin_time_format, convertView);
        TextView tv_watch_number_format = ViewHolder.get(R.id.tv_watch_number_format, convertView);

        SDViewBinder.setTextView(tv_title, model.getTitle());
        SDViewBinder.setTextView(tv_begin_time_format, model.getBegin_time_format());
        SDViewBinder.setTextView(tv_watch_number_format, model.getWatch_number_format());
    }
}
