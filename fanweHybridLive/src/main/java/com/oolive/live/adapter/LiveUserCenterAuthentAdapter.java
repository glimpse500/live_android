package com.oolive.live.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.live.R;
import com.oolive.live.model.App_AuthentItemModel;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25 0025.
 */
public class LiveUserCenterAuthentAdapter extends SDSimpleAdapter<App_AuthentItemModel> {
    public LiveUserCenterAuthentAdapter(List<App_AuthentItemModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_live_user_center_authent;
    }

    @Override
    public void bindData(int position, View convertView, ViewGroup parent, final App_AuthentItemModel model) {
        TextView item_authentication_type = get(R.id.item_authentication_type, convertView);
        SDViewBinder.setTextView(item_authentication_type, model.getName());
    }
}
