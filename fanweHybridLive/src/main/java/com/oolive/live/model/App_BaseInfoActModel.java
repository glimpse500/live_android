package com.oolive.live.model;

import com.oolive.hybrid.model.BaseActModel;

import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class App_BaseInfoActModel extends BaseActModel {
    private List<UserModel> list;

    public List<UserModel> getList() {
        return list;
    }

    public void setList(List<UserModel> list) {
        this.list = list;
    }
}
