package com.allever.sticker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Allever
 * @date 18/1/2
 */

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static File getNewFile(Context context, String folderName) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);

        String timeStamp = simpleDateFormat.format(new Date());

        String path;
        if (isSDAvailable()) {
            path = getFolderName(folderName) + File.separator + timeStamp + ".jpg";
        } else {
            path = context.getFilesDir().getPath() + File.separator + timeStamp + ".jpg";
        }

        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Log.d(TAG, "getNewFile: path = " + path);
        return new File(path);
    }

    /**
     * 判断sd卡是否可以用
     */
    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFolderName(String name) {
        File mediaStorageDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        name);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return "";
            }
        }
        return mediaStorageDir.getAbsolutePath();
    }

    /**
     *  获取指定目录的所有文件路径*/
    public static List<String> getAllFilePath(String parentDir){
        List<String> allFilePath = new ArrayList<>();
        File parentFile = new File(parentDir);
        File[] subFile = parentFile.listFiles();
        if (subFile == null) {
            return allFilePath;
        }

        for (File file: subFile) {
            // 判断是否为文件夹
            if (!file.isDirectory()) {
                String filename = file.getName();
                allFilePath.add(parentDir+"/" + filename);
            }
        }
        return allFilePath;
    }

    /**
     * 删除文件及文件夹*/
    public static void deleteFile(File file) {
        if (file.exists() == false) {
            Log.d(TAG, "deleteFile: 文件不存在");
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }

    public static void createDir(String path){
        File file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }
    }

    public static String getFileNameByUrl(String url){
        return url.substring(url.lastIndexOf("/")).split("/")[1];
    }

    public static String getStoreDir(Context context){
        String storeDir = context.getFilesDir() + "/store";
        File file = new File(storeDir);
        if (!file.exists()){
            file.mkdir();
        }
        return storeDir;
    }

    public static File saveImageFile(@NonNull File file, @NonNull Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("bmp should not be null");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "saveImageToGallery: the path of bmp is " + file.getAbsolutePath());
        return file;
    }
}
