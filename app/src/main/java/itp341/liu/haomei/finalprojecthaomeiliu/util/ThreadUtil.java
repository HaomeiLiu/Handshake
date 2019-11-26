package itp341.liu.haomei.finalprojecthaomeiliu.util;


import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
    static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    public static void runInUiThread(Runnable task) {
        mHandler.post(task);
    }
}