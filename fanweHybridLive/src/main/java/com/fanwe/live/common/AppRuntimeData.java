package com.fanwe.live.common;

/**
 * 用于保存app运行期间需要频繁用到的数据
 */
public class AppRuntimeData {
    private static AppRuntimeData instance;

    private AppRuntimeData() {
    }

    public static AppRuntimeData getInstance() {
        if (instance == null) {
            instance = new AppRuntimeData();
        }
        return instance;
    }
}
