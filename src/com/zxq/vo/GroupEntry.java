package com.zxq.vo;

/**
 * Created by zxq on 2014/10/12.
 */
public class GroupEntry {
    private String iconUrl;
    private String title;

    public GroupEntry() {
    }

    public GroupEntry(String iconUrl, String title) {
        this.iconUrl = iconUrl;
        this.title = title;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconUrl() {

        return iconUrl;
    }

    public String getTitle() {
        return title;
    }

}
