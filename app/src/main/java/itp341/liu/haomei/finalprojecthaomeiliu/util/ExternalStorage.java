package itp341.liu.haomei.finalprojecthaomeiliu.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

class ExternalStorage {
    /**
     * External Storage Root Directory
     */
    private String sdkStorageRoot = null;

    private static ExternalStorage instance;

    private ExternalStorage() {

    }

    synchronized public static ExternalStorage getInstance() {
        if (instance == null) {
            instance = new ExternalStorage();
        }
        return instance;
    }

    public void init(Context context, String sdkStorageRoot) {
        if (!TextUtils.isEmpty(sdkStorageRoot)) {
            File dir = new File(sdkStorageRoot);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (dir.exists() && !dir.isFile()) {
                this.sdkStorageRoot = sdkStorageRoot;
                if (!sdkStorageRoot.endsWith("/")) {
                    this.sdkStorageRoot = sdkStorageRoot + "/";
                }
            }
        }

        if (TextUtils.isEmpty(this.sdkStorageRoot)) {
            loadStorageState(context);
        }

        createSubFolders();
    }

    private void loadStorageState(Context context) {
        String externalPath = Environment.getExternalStorageDirectory().getPath();
        this.sdkStorageRoot = externalPath + "/" + context.getPackageName() + "/";
    }

    private void createSubFolders() {
        boolean result = true;
        File root = new File(sdkStorageRoot);
        if (root.exists() && !root.isDirectory()) {
            root.delete();
        }
        for (StorageType storageType : StorageType.values()) {
            result &= makeDirectory(sdkStorageRoot + storageType.getStoragePath());
        }
        if (result) {
            createNoMediaFile(sdkStorageRoot);
        }
    }

    /**
     * mkdir
     *
     * @param path
     * @return exist
     */
    private boolean makeDirectory(String path) {
        File file = new File(path);
        boolean exist = file.exists();
        if (!exist) {
            exist = file.mkdirs();
        }
        return exist;
    }

    protected static String NO_MEDIA_FILE_NAME = ".nomedia";

    private void createNoMediaFile(String path) {
        File noMediaFile = new File(path + "/" + NO_MEDIA_FILE_NAME);
        try {
            if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getWritePath: convert fileName (String) to absolute write path (String)
     *
     * @param fileName
     *            Full name of the file (filename.extension)
     * @return abs write path info
     */
    public String getWritePath(String fileName, StorageType fileType) {
        return pathForName(fileName, fileType, false, false);
    }

    private String pathForName(String fileName, StorageType type, boolean dir,
                               boolean check) {
        String directory = getDirectoryByDirType(type);
        StringBuilder path = new StringBuilder(directory);

        if (!dir) {
            path.append(fileName);
        }

        String pathString = path.toString();
        File file = new File(pathString);

        if (check) {
            if (file.exists()) {
                if ((dir && file.isDirectory())
                        || (!dir && !file.isDirectory())) {
                    return pathString;
                }
            }

            return "";
        } else {
            return pathString;
        }
    }

    /**
     * return path for certain file type
     *
     * @param fileType
     * @return
     */
    public String getDirectoryByDirType(StorageType fileType) {

        return sdkStorageRoot + fileType.getStoragePath();
    }

    /**
     * Find the full path based on given fileName and fileType
     * @param fileName
     * @param fileType
     * @return if the file exists, return the path. If not, return empty string
     */
    public String getReadPath(String fileName, StorageType fileType) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        return pathForName(fileName, fileType, false, true);
    }

}
