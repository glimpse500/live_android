package com.fanwe.pay.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.fanwe.live.R;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.dialog.common.AppDialogConfirm;

/**
 * Created by Administrator on 2017/1/5.
 */

public class LiveScenePriceDialog extends AppDialogConfirm {
    private EditText et_diamond;

    private int live_pay_scene_max;
    private int live_pay_scene_min;

    public int getLive_pay_scene_min() {
        return live_pay_scene_min;
    }

    public void setLive_pay_scene_min(int live_pay_scene_min) {
        this.live_pay_scene_min = live_pay_scene_min;
    }

    public LiveScenePriceDialog(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        setTextTitle("按场付费定价");
        setCancelable(false);
        setDismissAfterClick(false);
        setCustomView(R.layout.dialog_live_scene_price);
        findView();
        initInputLimit();
        register();
    }

    private void findView() {
        et_diamond = (EditText) findViewById(R.id.et_diamond);
    }

    private void initInputLimit() {
        live_pay_scene_max = AppRuntimeWorker.getLivePaySceneMax();
        live_pay_scene_min = AppRuntimeWorker.getLivePaySceneMin();

        String text_center = live_pay_scene_min + "<=价格";
        if (live_pay_scene_max > 0) {
            text_center = text_center + "<=" + live_pay_scene_max;
        }
        et_diamond.setHint(text_center);
    }

    private void register() {
        et_diamond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = et_diamond.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    int int_text = Integer.valueOf(text);
                    if (int_text == 0) {
                        et_diamond.setText("1");
                        et_diamond.setSelection(1);
                        return;
                    }

                    if (live_pay_scene_max > 0) {
                        if (int_text > live_pay_scene_max) {
                            String s_text = String.valueOf(live_pay_scene_max);
                            et_diamond.setText(s_text);
                            et_diamond.setSelection(s_text.length());
                            return;
                        }
                    }
                    return;
                }
            }
        });
    }

    /**
     * 重置最低价格
     */
    public void resetMinPrice() {
        et_diamond.setText(Integer.toString(live_pay_scene_min));
        et_diamond.setSelection(Integer.toString(live_pay_scene_min).length());
    }

    //获取输入框价格
    public int getImportPrice() {
        int price = 1;
        if (et_diamond != null) {
            String diamond = et_diamond.getText().toString();
            if (!TextUtils.isEmpty(diamond)) {
                int int_diamond = Integer.valueOf(diamond);
                if (int_diamond > 0) {
                    return int_diamond;
                }
            }
        }
        return price;
    }
}
