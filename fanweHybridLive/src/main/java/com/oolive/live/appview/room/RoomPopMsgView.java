package com.oolive.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;

import com.oolive.library.utils.LogUtil;
import com.oolive.live.R;
import com.oolive.live.model.custommsg.CustomMsgPopMsg;
import com.oolive.live.view.LivePopMsgView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 弹幕消息
 *
 * @author Administrator
 * @date 2016-5-20 下午5:21:09
 */
public class RoomPopMsgView extends RoomLooperMainView<CustomMsgPopMsg> {

    public RoomPopMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoomPopMsgView(Context context) {
        super(context);
    }

    private static final long DURATION_LOOPER = 500;

    private LivePopMsgView view_pop_msg0;
    private LivePopMsgView view_pop_msg1;

    private List<LivePopMsgView> listView;


    @Override
    protected int onCreateContentView() {
        return R.layout.view_room_pop_msg;
    }

    @Override
    protected void onBaseInit() {
        super.onBaseInit();
        view_pop_msg0 = find(R.id.view_pop_msg0);
        view_pop_msg1 = find(R.id.view_pop_msg1);

        listView = new ArrayList<>();

        listView.add(view_pop_msg0);
        listView.add(view_pop_msg1);
    }

    @Override
    public void onMsgPopMsg(CustomMsgPopMsg msg) {
        LogUtil.i("CustomMsgPopMsg :" +  msg.getDesc());
        super.onMsgPopMsg(msg);
        offerModel(msg);
    }

    @Override
    protected long getLooperPeriod() {
        return DURATION_LOOPER;
    }

    @Override
    protected void onLooperWork(LinkedList<CustomMsgPopMsg> queue) {
        for (LivePopMsgView item : listView) {
            LogUtil.i("onLooperWork = ");
            if (item.canPlay()) {
                LogUtil.i("item.canPlay() = ");
                item.playMsg(queue.poll());
            }
        }
    }

}
