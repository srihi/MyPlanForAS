package com.andwho.myplan.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by ys_1shawn on 2016/2/14.
 */
public class Banner extends BmobObject implements Serializable {


    public String title;//
    public String isDeleted;//是否已经删除 1是 0否
    public String detailURL;//
    public String imgURL;
    public String readTimes;
    public String bannerType;

}