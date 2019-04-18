package com.oolive.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.oolive.hybrid.http.AppHttpUtil;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.http.AppRequestParams;
import com.fanwe.lib.animator.SDAnim;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.oolive.live.IMHelper;
import com.oolive.live.R;
import com.oolive.live.appview.LiveSendGiftView;
import com.oolive.live.common.CommonInterface;
import com.oolive.live.model.App_pop_propActModel;
import com.oolive.live.model.Deal_send_propActModel;
import com.oolive.live.model.LiveGiftModel;
import com.oolive.live.model.custommsg.CustomMsgPrivateGift;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import org.xutils.common.util.LogUtil;

public class RoomSendGiftView extends RoomView {
    public RoomSendGiftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RoomSendGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoomSendGiftView(Context context) {
        super(context);
    }

    private LiveSendGiftView view_send_gift;

    private SDAnim mAnimVisible;
    private SDAnim mAnimInvisible;

    @Override
    protected int onCreateContentView() {
        return R.layout.view_room_send_gift;
    }

    @Override
    protected void onBaseInit() {
        super.onBaseInit();

        view_send_gift = (LiveSendGiftView) findViewById(R.id.view_send_gift);
        view_send_gift.setCallback(new LiveSendGiftView.SendGiftViewCallback() {
            @Override
            public void onClickSend(LiveGiftModel model, int is_plus) {
                sendGift(model, is_plus);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mAnimVisible == null) {
            mAnimVisible = SDAnim.from(this);
            setVisibleAnimator(mAnimVisible.get());
        }
        mAnimVisible.translationY(h, 0);

        if (mAnimInvisible == null) {
            mAnimInvisible = SDAnim.from(this);
            setInvisibleAnimator(mAnimInvisible.get());
        }
        mAnimInvisible.translationY(0, h);
    }

    private void sendGift(final LiveGiftModel giftModel, int is_plus) {
        LogUtil.i("-----------Gift");
        if (giftModel != null) {
            if (getLiveActivity().getRoomInfo() == null) {
                return;
            }
            LogUtil.d(" Room sendGift");
            if (getLiveActivity().getRoomInfo().getLive_in() == 0) {
                //私聊发礼物接口
                final String createrId = getLiveActivity().getCreaterId();
                if (createrId != null) {
                    CommonInterface.requestSendGiftPrivate(giftModel.getId(), 1, createrId, new AppRequestCallback<Deal_send_propActModel>() {
                        @Override
                        protected void onSuccess(SDResponse resp) {
                            if (actModel.isOk()) {
                                view_send_gift.sendGiftSuccess(giftModel);

                                // 发送私聊消息给主播
                                final CustomMsgPrivateGift msg = new CustomMsgPrivateGift();
                                msg.fillData(actModel);
                                IMHelper.sendMsgC2C(createrId, msg, new TIMValueCallBack<TIMMessage>() {
                                    @Override
                                    public void onError(int i, String s) {
                                    }

                                    @Override
                                    public void onSuccess(TIMMessage timMessage) {
                                        // 如果私聊界面不是每次都加载的话要post一条来刷新界面
                                        // IMHelper.postMsgLocal(msg, createrId);
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                AppRequestParams params = CommonInterface.requestSendGiftParams(giftModel.getId(), 1, is_plus, getLiveActivity().getRoomId());
                AppHttpUtil.getInstance().post(params, new AppRequestCallback<App_pop_propActModel>() {
                    @Override
                    protected void onSuccess(SDResponse resp) {
                        LogUtil.d(" Room sendGift onSuccess" + resp.getResult());
                        // 扣费
                        if (actModel.isOk()) {
                            view_send_gift.sendGiftSuccess(giftModel);
                        }
                    }

                    @Override
                    protected void onError(SDResponse resp) {
                        LogUtil.d(" Room sendGift onError" + resp.getResult());
                        CommonInterface.requestMyUserInfo(null);
                    }
                });
            }
        }
    }

    public void bindData() {
        if (view_send_gift != null) {
            view_send_gift.requestData();
            view_send_gift.bindUserData();
        }
    }

    @Override
    protected boolean onTouchDownOutside(MotionEvent ev) {
        getVisibilityHandler().setInvisible(true);
        return true;
    }

    @Override
    public boolean onBackPressed() {
        getVisibilityHandler().setInvisible(true);
        return true;
    }
}
