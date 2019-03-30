package com.oolive.live.model;

import com.oolive.hybrid.model.BaseActListModel;

import java.util.List;

/**
 * Created by HSH on 2016/7/15.
 */
public class Settings_black_listActModel extends BaseActListModel {
    private List<UserModel> user;

    public List<UserModel> getUser() {
        return user;
    }

    public void setUser(List<UserModel> user) {
        this.user = user;
    }
}
