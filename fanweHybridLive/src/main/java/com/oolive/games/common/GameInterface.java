package com.oolive.games.common;

import com.oolive.games.model.Games_autoStartActModel;
import com.oolive.hybrid.http.AppHttpUtil;
import com.oolive.hybrid.http.AppRequestCallback;
import com.oolive.hybrid.http.AppRequestParams;

/**
 * 游戏相关接口
 */
public class GameInterface {

    /**
     * 请求设置自动开始游戏
     *
     * @param auto_start 1-自动开始；0-手动开始
     * @param callback
     */
    public static void requestAutoStartGame(int auto_start, AppRequestCallback<Games_autoStartActModel> callback) {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("games");
        params.putAct("autoStart");
        params.put("auto_start", auto_start);
        AppHttpUtil.getInstance().post(params, callback);
    }

}
