package com.allever.sticker.network;

import com.allever.sticker.bean.StickerDetailData;
import com.allever.sticker.bean.StoreStickerData;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 *
 * @author Allever
 * @date 18/2/10
 */

public interface NetworkService {
    @GET("master/data/izhifei/stickercamera/storedata")
    Observable<StoreStickerData> getStoreSticker();

    @GET("master/data/izhifei/stickercamera/store/{stickername}/data")
    Observable<StickerDetailData> getStickerDetail(@Path("stickername") String name);
}
