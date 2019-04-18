package com.oolive.live.model.custommsg;

import com.oolive.library.utils.SDToast;
import com.oolive.live.model.UserModel;
import com.oolive.live.LiveConstant;

public class CustomMsgViewerJoin extends CustomMsg {

    public CustomMsgViewerJoin() {
        super();
        setType(LiveConstant.CustomMsgType.MSG_VIEWER_JOIN);
    }

    public void setSortNumber(int sortNumber) {
        UserModel userModel = getSender();
        if (userModel == null) {
            SDToast.showToast("setSort_num fail usermodel is null");
            return;
        }
        if (sortNumber <= 0) {
            sortNumber = userModel.getUser_level();
        }
        userModel.setSort_num(sortNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof CustomMsgViewerJoin)) {
            return false;
        }

        CustomMsgViewerJoin model = (CustomMsgViewerJoin) o;
        UserModel user = model.getSender();
        if (user == null) {
            return false;
        }
        if (!user.equals(getSender())) {
            return false;
        }

        return true;
    }

}
