package com.oolive.live.model;

import com.oolive.hybrid.model.BaseActModel;

import java.util.List;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-6-23 下午9:16:56 类说明
 */
@SuppressWarnings("serial")
public class App_my_follow_ActModel extends BaseActModel {
    private List<UserModel> list;

    public List<UserModel> getList() {
        return list;
    }

    public void setList(List<UserModel> list) {
        this.list = list;
    }

}
