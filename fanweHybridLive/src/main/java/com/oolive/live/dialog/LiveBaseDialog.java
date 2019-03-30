package com.oolive.live.dialog;

import android.app.Activity;

import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.oolive.live.R;
import com.oolive.live.activity.room.ILiveActivity;

public class LiveBaseDialog extends SDDialogBase {
    public LiveBaseDialog(Activity activity) {
        super(activity, R.style.dialogBase);
    }

    public ILiveActivity getLiveActivity() {
        if (getOwnerActivity() instanceof ILiveActivity) {
            return (ILiveActivity) getOwnerActivity();
        } else {
            return null;
        }
    }
}
