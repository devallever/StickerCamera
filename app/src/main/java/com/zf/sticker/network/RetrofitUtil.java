package com.zf.sticker.network;

import com.zf.sticker.bean.StickerDetailData;
import com.zf.sticker.bean.StickerDetailItem;
import com.zf.sticker.bean.StoreStickerData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * @author Allever
 * @date 2017/1/15
 */

public class RetrofitUtil {
    private static final String TAG = "RetrofitUtil";
    private static final String BASE_URL = "https://raw.githubusercontent.com/devallever/DataProject/";
    private Retrofit mRetrofit;
    private NetworkService mNetworkService;

    private RetrofitUtil(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mNetworkService = mRetrofit.create(NetworkService.class);
    }

    public static RetrofitUtil getInstance(){
        return RetrofitHolder.INSTANCE;
    }

    public void getStoreSticker(Observer<StoreStickerData> subscriber){
        mNetworkService.getStoreSticker()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber);

    }

    public void getStickerDetail(String name, Observer<StickerDetailData> subscriber){
        mNetworkService.getStickerDetail(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(subscriber);

    }

    private static class RetrofitHolder{
        private static final RetrofitUtil INSTANCE = new RetrofitUtil();
    }
}
