package itp341.liu.haomei.finalprojecthaomeiliu.im;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.LoginActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.util.DialogCreator;
import itp341.liu.haomei.finalprojecthaomeiliu.util.FileHelper;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;

//TODO: Unused

public class BaseActivity extends AppCompatActivity {

    protected int mWidth;
    protected int mHeight;
    protected float mDensity;
    protected int mDensityDpi;
    private TextView menu_title_textview;
    private ImageButton menu_return_button;
    protected int mAvatarSize;
    protected float mRatio;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //register sdk's event to receive events
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);
    }

    //初始化各个activity的title
    public void initTitle(boolean returnBtn, boolean titleLeftDesc, String titleLeft, String title, boolean save, String desc) {
        menu_return_button =  findViewById(R.id.return_btn);
        menu_title_textview = findViewById(R.id.title);

        if (returnBtn) {
            menu_return_button.setVisibility(View.VISIBLE);
            menu_return_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive() && getCurrentFocus() != null) {
                        if (getCurrentFocus().getWindowToken() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    finish();
                }
            });
        }
        if (titleLeftDesc) {
//            mJmui_title_left.setVisibility(View.VISIBLE);
//            mJmui_title_left.setText(titleLeft);
        }
        menu_title_textview.setText(title);
        if (save) {
//            mJmui_commit_btn.setVisibility(View.VISIBLE);
//            mJmui_commit_btn.setText(desc);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public void goToActivity(Context context, Class toActivity) {
        Intent intent = new Intent(context, toActivity);
        startActivity(intent);
        finish();
    }

    public void onEventMainThread(LoginStateChangeEvent event) {
        final LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        if (myInfo != null) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }
            SharePreferenceManager.setCachedUsername(myInfo.getUserName());
            SharePreferenceManager.setCachedAvatarPath(path);
            JMessageClient.logout();
        }
        switch (reason) {
//            case user_logout:
//                View.OnClickListener listener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        switch (v.getId()) {
//                            case R.id.jmui_cancel_btn:
//                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                break;
//                            case R.id.jmui_commit_btn:
//                                JMessageClient.login(SharePreferenceManager.getCachedUsername(), SharePreferenceManager.getCachedPsw(), new BasicCallback() {
//                                    @Override
//                                    public void gotResult(int responseCode, String responseMessage) {
//                                        if (responseCode == 0) {
//                                            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }
//                                });
//                                break;
//                        }
//                    }
//                };
//                dialog = DialogCreator.createLogoutStatusDialog(BaseActivity.this, "您的账号在其他设备上登陆", listener);
//                dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.show();
//                break;
            case user_password_change:
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onDestroy() {
        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this);
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

}
