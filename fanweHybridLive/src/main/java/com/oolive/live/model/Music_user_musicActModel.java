package com.oolive.live.model;

import com.oolive.hybrid.model.BaseActListModel;

import java.util.List;

/**
 * 用户音乐列表
 *
 * @author ldh
 */
@SuppressWarnings("serial")
public class Music_user_musicActModel extends BaseActListModel {
    private List<LiveSongModel> list;

    public List<LiveSongModel> getList() {
        return list;
    }

    public void setList(List<LiveSongModel> list) {
        this.list = list;
    }


}
