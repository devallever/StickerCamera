package com.zf.sticker.network;

import com.zf.sticker.bean.StickerDetailData;
import com.zf.sticker.bean.StoreStickerData;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mac on 18/2/10.
 */

public interface NetworkService {
    @GET("master/data/izhifei/stickercamera/storedata")
    Observable<StoreStickerData> getStoreSticker();

    @GET("master/data/izhifei/stickercamera/store/{stickername}/data")
    Observable<StickerDetailData> getStickerDetail(@Path("stickername") String name);
}
