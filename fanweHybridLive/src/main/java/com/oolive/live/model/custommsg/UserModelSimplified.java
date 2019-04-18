package com.oolive.live.model.custommsg;

import com.oolive.live.model.UserModel;

public class UserModelSimplified {
    //only left useful data
    private String user_id = ""; // 用户id
    private String head_image; // 头像
    private String nick_name; // 昵称    private int sex; // 0-未知，1-男，2-女
    private String v_icon;// 认证图标
    private int user_level; // 用户等级
    private int sex; // 0-未知，1-男，2-女
    public UserModel compress(){
        UserModel compress_model = new UserModel();
        String id = compress_model.getUser_id();

        compress_model.setUser_id(this.getUser_id());
        compress_model.setHead_image(this.getHead_image());
        compress_model.setNick_name(this.getNick_name());
        compress_model.setSex(this.getSex());
        compress_model.setV_icon(this.getV_icon());
        compress_model.setUser_level(this.getUser_level());
        return compress_model;
    }
    public void setUser_id(String user_id) {
        if (user_id != null) {
            this.user_id = user_id;
        }
    }
    public String getUser_id() {
        return user_id;
    }
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }


    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getV_icon() {
        return v_icon;
    }

    public void setV_icon(String v_icon) {
        this.v_icon = v_icon;
    }
    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }

}
