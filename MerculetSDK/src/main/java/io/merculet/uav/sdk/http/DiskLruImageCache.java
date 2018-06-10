package io.merculet.uav.sdk.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.merculet.uav.sdk.log.DebugLog;
import io.merculet.uav.sdk.util.FileUtils;
import io.merculet.uav.sdk.util.IOUtils;
import io.merculet.uav.sdk.util.StringUtils;


/**
 * Created by Tony Shen on 2016/11/13.
 */

public class DiskLruImageCache {

    private static final String TAG = "DiskLruImageCache";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static int mCompressQuality = 70;
    private DiskLruCache mDiskCache;

    public DiskLruImageCache(Context context) {
        this(context, mCompressQuality);
    }

    public DiskLruImageCache(Context context, int quality) {
        if (context == null)
            return;
        try {
            if (mDiskCache == null) {
                File cacheDir = FileUtils.getDiskCacheDir(context, FileUtils.FILE_SAVE_PATH);
                if (cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) {
                    mDiskCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, DISK_CACHE_SIZE);
                    mCompressQuality = quality;
                } else {
                    mDiskCache = null;
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
            DebugLog.e(e.getMessage());
        }
    }


    private int getAppVersion(Context context) {
//        int result = 1;
//        try {
//            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            if (info != null) {
//                result = info.versionCode;
//            }
//        } catch (PackageManager.NameNotFoundException ignored) {
//        }
        return 1;
    }

    public void put(final String key, final Bitmap data) {

        if (data == null || mDiskCache == null)
            return;

        OutputStream out = null;
        String ekey = StringUtils.md5(key);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                DiskLruCache.Editor editor = mDiskCache.edit(ekey);
                if (editor == null)
                    return;
                out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
                Bitmap.CompressFormat format;
                if (key.endsWith("png") || key.endsWith("PNG")) {
                    format = Bitmap.CompressFormat.PNG;
                } else if (Build.VERSION.SDK_INT >= 14 && key.endsWith("webp")) {
                    format = Bitmap.CompressFormat.WEBP;
                } else {
                    format = Bitmap.CompressFormat.JPEG;
                }
                data.compress(format, mCompressQuality, out);
                editor.commit();
                mDiskCache.flush();
            } else {
                snapshot.getInputStream(0).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public Bitmap getBitmap(final String key) {

        if (mDiskCache == null)
            return null;

        DiskLruCache.Snapshot snapShot = null;

        try {
            snapShot = mDiskCache.get(StringUtils.md5(key));
        } catch (IOException e) {
            return null;
        }
        if (snapShot != null) {
            InputStream is = snapShot.getInputStream(0);
            if (is != null) {
                BufferedInputStream buffIn = new BufferedInputStream(is, IO_BUFFER_SIZE);
                return BitmapFactory.decodeStream(buffIn);
            }
        }

        return null;
    }

    public void clearCache() {
        if (mDiskCache == null)
            return;
        try {
            mDiskCache.delete();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
