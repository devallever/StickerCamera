package com.allever.sticker.event;

/**
 *
 * @author Allever
 * @date 18/2/10
 */

public class DeleteStickerEvent {
    private String name;
    private int position;

    public DeleteStickerEvent(String name, int position){
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
