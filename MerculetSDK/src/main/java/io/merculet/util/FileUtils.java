package io.merculet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 文件操作
 *
 * @author aaron
 * @since 14/6/19
 */
public class FileUtils {
    /**
     * 文件保存路径
     */
    public static final String FILE_SAVE_PATH = "mw_cache";

    private static final String TAG = "FileUtils";

    public static String getMWCachePath(final Context context) {
        return getDiskCacheDir(context, FILE_SAVE_PATH).getPath() + File.separator;
    }

    /**
     * 加载系统本地图片
     */
    @SuppressWarnings("unused")
    public static Bitmap loadImage(final String url, final String filename) {
        try {
            FileInputStream fis = new FileInputStream(url + filename);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long totalBlocks = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();

        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }
        return totalBlocks * blockSize;
    }

    private static boolean hasExternalCache(Context context) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && Util.checkPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE")
                && context.getExternalCacheDir() != null;
    }

    public static File getDiskCacheDir(final Context context, String fileDir) {

        File cacheDirectory;
        if (hasExternalCache(context)) {
            cacheDirectory = context.getExternalCacheDir();
        } else {
            cacheDirectory = context.getCacheDir();
        }
        if (cacheDirectory == null) {
            cacheDirectory = context.getCacheDir();
            if (cacheDirectory == null) {
                return null;
            }
        }
        if (fileDir != null) {
            File file = new File(cacheDirectory, fileDir);
            if (!file.exists() && !file.mkdir()) {
                return cacheDirectory;
            } else {
                return file;
            }
        }
        return cacheDirectory;
    }
}
