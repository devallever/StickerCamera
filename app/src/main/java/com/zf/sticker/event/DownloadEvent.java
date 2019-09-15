package com.zf.sticker.event;

/**
 * Created by Mac on 18/2/24.
 */

public class DownloadEvent {
    private int progress;
    public DownloadEvent(int progress){
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
