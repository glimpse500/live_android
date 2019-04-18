package com.oolive.live.activity;

import com.oolive.hybrid.activity.BaseTitleActivity;
import com.oolive.live.model.App_ProfitBindingActModel;

/**
 * Created by yhz on 2017/6/22.
 */

public abstract class LiveMobileBindBaseActivity extends BaseTitleActivity {
    protected abstract void requestMobileBind(String code);

    protected abstract void requestOnSuccess(App_ProfitBindingActModel actModel);
}
