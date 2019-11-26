package itp341.liu.haomei.finalprojecthaomeiliu.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import itp341.liu.haomei.finalprojecthaomeiliu.R;

public class ViewDialog {

    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ui_loading_view);

        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }

}
