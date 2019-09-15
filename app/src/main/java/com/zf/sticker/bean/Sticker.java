package com.zf.sticker.bean;

import java.util.List;

/**
 *
 * @author Allever
 * @date 2017/12/31
 */

public class Sticker {
    private List<String> stickerPathList;
    private String typePath;
    private int[] resId;
    private String title;
    private int typeResId;

    public int[] getResId() {
        return resId;
    }

    public void setResId(int[] resId) {
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTypeResId() {
        return typeResId;
    }

    public void setTypeResId(int typeResId) {
        this.typeResId = typeResId;
    }

    public List<String> getStickerPathList() {
        return stickerPathList;
    }

    public void setStickerPathList(List<String> stickerPathList) {
        this.stickerPathList = stickerPathList;
    }

    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }
}
