package com.zf.sticker.bean;

/**
 *
 * @author Allever
 * @date 18/2/10
 */

public class StickerDetailItem {
    private String name;
    private String typeurl;
    private String downloadurl;
    private String fitststickurl;
    private String size;
    private String[] sticklisturl;

    public String getName() {
        return name;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeurl() {
        return typeurl;
    }

    public void setTypeurl(String typeurl) {
        this.typeurl = typeurl;
    }

    public String getFitststickurl() {
        return fitststickurl;
    }

    public void setFitststickurl(String fitststickurl) {
        this.fitststickurl = fitststickurl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String[] getSticklisturl() {
        return sticklisturl;
    }

    public void setSticklisturl(String[] sticklisturl) {
        this.sticklisturl = sticklisturl;
    }
}
