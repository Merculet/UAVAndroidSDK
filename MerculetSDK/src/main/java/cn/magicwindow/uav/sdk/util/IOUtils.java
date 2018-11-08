package cn.magicwindow.uav.sdk.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Tony Shen on 16/5/25.
 */
public class IOUtils {

    /**
     * 安全关闭io流
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
