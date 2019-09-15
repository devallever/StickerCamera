package com.zf.sticker.bean;

import java.util.List;

/**
 * Created by Mac on 18/2/9.
 */

public class StickerData {
    private String stickerName;
    private List<String> stickerPaths;

    public String getStickerName() {
        return stickerName;
    }

    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }

    public List<String> getStickerPaths() {
        return stickerPaths;
    }

    public void setStickerPaths(List<String> stickerPaths) {
        this.stickerPaths = stickerPaths;
    }
}
