package com.oolive.live.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oolive.library.adapter.SDSimpleAdapter;
import com.oolive.library.utils.LogUtil;
import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.library.utils.ViewHolder;
import com.oolive.live.R;
import com.oolive.live.model.LiveConversationListModel;
import com.oolive.live.utils.GlideUtil;
import com.oolive.live.view.LiveUnReadNumTextView;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-15 下午2:14:28 类说明
 */
public class LiveConversationListAdapter extends SDSimpleAdapter<LiveConversationListModel> {
    public LiveConversationListAdapter(List<LiveConversationListModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_live_chat_user;
    }

    @Override
    public void bindData(final int position, View convertView, ViewGroup parent, final LiveConversationListModel model) {
        ImageView civ_v_icon = ViewHolder.get(R.id.civ_v_icon, convertView);
        SDViewUtil.setGone(civ_v_icon);

        ImageView civ_head_image = ViewHolder.get(R.id.civ_head_image, convertView);
        TextView tv_nick_name = ViewHolder.get(R.id.tv_nick_name, convertView);
        TextView tv_content = ViewHolder.get(R.id.tv_content, convertView);
        ImageView iv_global_male = ViewHolder.get(R.id.iv_global_male, convertView);
        ImageView iv_rank = ViewHolder.get(R.id.iv_rank, convertView);
        TextView tv_time = ViewHolder.get(R.id.tv_time, convertView);
        LiveUnReadNumTextView tv_unreadnum = ViewHolder.get(R.id.tv_unreadnum, convertView);

        LogUtil.i("tv_time = "+ model.getTimeFormat() );
        SDViewBinder.setTextView(tv_time, model.getTimeFormat());
        tv_content.setText(model.getText());
        LogUtil.i("tv_unreadnum = "+ model.getUnreadNum() );
        if (model.getUnreadNum() > 0) {
            SDViewUtil.setVisible(tv_unreadnum);
            tv_unreadnum.setUnReadNumText(model.getUnreadNum());
        } else {
            SDViewUtil.setGone(tv_unreadnum);
        }

        SDViewBinder.setTextView(tv_nick_name, model.getNick_name());
        iv_global_male.setImageResource(model.getSexResId());
        iv_rank.setImageResource(model.getLevelImageResId());
        LogUtil.i("model.getHead_image()" + model.getHead_image());
        GlideUtil.loadHeadImage(model.getHead_image()).into(civ_head_image);

        if (!TextUtils.isEmpty(model.getV_icon())) {
            SDViewUtil.setVisible(civ_v_icon);
            GlideUtil.load(model.getV_icon()).into(civ_v_icon);
        } else {
            SDViewUtil.setGone(civ_v_icon);
        }
        convertView.setOnClickListener(this);
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notifyItemLongClickCallback(position, model, v);
                return true;
            }
        });
    }
}
