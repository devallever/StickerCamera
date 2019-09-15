package com.allever.sticker.bean;

/**
 *
 * @author Allever
 * @date 18/2/8
 */

public class StoreStickerItem {
    private int id;
    String name;
    private String url;
    private boolean downloaded;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
