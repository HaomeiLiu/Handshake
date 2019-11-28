package itp341.liu.haomei.finalprojecthaomeiliu.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void shortToast(Context context, String desc) {
        Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
    }
}
