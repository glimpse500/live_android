package com.oolive.live.appview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.oolive.library.utils.SDViewBinder;
import com.oolive.library.utils.SDViewUtil;
import com.oolive.live.R;
import com.oolive.live.model.LiveRoomModel;
import com.oolive.live.utils.GlideUtil;

/**
 * Created by Administrator on 2017/8/1.
 */

public class ItemLiveTabCategorySingle extends BaseAppView {

    private ImageView iv_room_image;
    private TextView tv_city;
    private LiveRoomModel model;

    public ItemLiveTabCategorySingle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ItemLiveTabCategorySingle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemLiveTabCategorySingle(Context context) {
        super(context);
        init();
    }

    protected void init() {
        setContentView(R.layout.item_live_tab_category_single);

        iv_room_image = (ImageView) findViewById(R.id.iv_room_image);
        tv_city = (TextView) findViewById(R.id.tv_city);
    }

    public LiveRoomModel getModel() {
        return model;
    }

    public void setModel(LiveRoomModel model) {
        this.model = model;
        if (model != null) {
            SDViewUtil.setVisible(this);
            GlideUtil.load(model.getLive_image()).into(iv_room_image);
            SDViewBinder.setTextView(tv_city, model.getCity());
        } else {
            SDViewUtil.setGone(this);
        }
    }

}
