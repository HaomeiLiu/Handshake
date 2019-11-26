package itp341.liu.haomei.finalprojecthaomeiliu.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class StorageUtil {
    public final static long K = 1024;
    public final static long M = 1024 * 1024;
    // 外置存储卡默认预警临界值
    private static final long THRESHOLD_WARNING_SPACE = 100 * M;
    // 保存文件时所需的最小空间的默认值
    public static final long THRESHOLD_MIN_SPCAE = 20 * M;

    public static void init(Context context, String rootPath) {
        ExternalStorage.getInstance().init(context, rootPath);
    }

    /**
     * Obtain the write path of the file, no toast
     *
     * @param fileName
     * @param fileType
     * @return usable write path or null
     */
    public static String getWritePath(String fileName, StorageType fileType) {
        return getWritePath(null, fileName, fileType, false);
    }

    /**
     * obtain write path of the file, a bool to enable toast
     *
     * @param fileName
     * @param tip, true for a warning toast
     * @return usable write path or null
     */
    private static String getWritePath(Context context, String fileName, StorageType fileType, boolean tip) {
        String path = ExternalStorage.getInstance().getWritePath(fileName, fileType);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File dir = new File(path).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String getWritePath(Context context, String fileName, StorageType fileType) {
        return getWritePath(context, fileName, fileType, true);
    }

    public static String getSystemImagePath() {
        if (Build.VERSION.SDK_INT > 7) {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            return picturePath + "/im/";
        } else {
            String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            return picturePath + "/im/";
        }
    }
}
