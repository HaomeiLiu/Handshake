package itp341.liu.haomei.finalprojecthaomeiliu.controller;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.LoginActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.RegisterActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.db.UserEntry;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;

import static itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication.isLogin;

public class LoginController implements View.OnClickListener{
    private LoginActivity mContext;
    public static final String TAG = LoginController.class.getSimpleName();


    public LoginController(LoginActivity loginActivity) {
        this.mContext = loginActivity;
    }

    private boolean whatStartWith(String str) {
        Pattern pattern = Pattern.compile("^([A-Za-z]|[0-9])");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    private boolean whatContain(String str) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z][a-zA-Z0-9_\\-@\\.]{3,127}$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //Login Authentication
                final String userId = mContext.getUserId();
                final String password = mContext.getPassword();
                if (TextUtils.isEmpty(userId)) {
                    ToastUtil.shortToast(mContext, "Username cannot be empty");
                    mContext.editTextUser.startAnimation(shakeAnimation(3));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.shortToast(mContext, "Password cannot be empty");
                    mContext.editTextPassword.startAnimation(shakeAnimation(3));
                    return;
                }
                if (userId.length() < 4 || userId.length() > 128) {
                    mContext.editTextUser.startAnimation(shakeAnimation(3));
                    ToastUtil.shortToast(mContext, "Username should have length between 4-128 characters");
                    return;
                }
                if (password.length() < 4 || password.length() > 128) {
                    mContext.editTextPassword.startAnimation(shakeAnimation(3));
                    ToastUtil.shortToast(mContext, "Password should have length between 4-128 characters");
                    return;
                }
                if (!whatStartWith(userId)) {
                    mContext.editTextUser.startAnimation(shakeAnimation(3));
                    ToastUtil.shortToast(mContext, "Username can only start with letter or number");
                    return;
                }
                if (!whatContain(userId)) {
                    mContext.editTextUser.startAnimation(shakeAnimation(3));
                    ToastUtil.shortToast(mContext, "Username can only contain number, letter, _ . - @");
                    return;
                }
                //Login
                if (isLogin) {
                    final ViewDialog dialog = new ViewDialog(mContext);
//                    final Dialog dialog = DialogCreator.createLoadingDialog(mContext,
//                            mContext.getString(R.string.ui_loading_text));
                    dialog.showDialog();
                    Log.d(TAG, "After dialog created and shown");
                    JMessageClient.login(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.hideDialog();
                            Log.d(TAG, "Login Result Back");

                            if (responseCode == 0) {
                                SharePreferenceManager.setCachedPsw(password);
                                UserInfo myInfo = JMessageClient.getMyInfo();
                                //Successfully logged in.
                                String username = myInfo.getUserName();
                                String appKey = myInfo.getAppKey();
                                UserEntry user = UserEntry.getUser(username, appKey);
                                if (null == user) {
                                    user = new UserEntry(username, appKey);
                                    user.save();
                                }
                                mContext.goToActivity(mContext, HomeActivity.class);
                                ToastUtil.shortToast(mContext, "Login Success");
                                mContext.finish();
                            } else {
                                ToastUtil.shortToast(mContext, "Login Failed. " + responseMessage);
                            }
                        }
                    });
                    //register
                }
                else {
                    Log.d(TAG, "register button pressed");
                    JMessageClient.register(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Log.d(TAG, "i=0");
                                SharePreferenceManager.setRegisterName(userId);
                                SharePreferenceManager.setRegistePass(password);
                                mContext.startActivity(new Intent(mContext, RegisterActivity.class));
                                ToastUtil.shortToast(mContext, "Successfully registered");
                            } else {
                                Log.d(TAG, "i!=0");
                                //HandleResponseCode.onHandle(mContext, i, false);
                                ToastUtil.shortToast(mContext, "Register Failed");
                            }
                        }
                    });
                }
                break;
            case R.id.register_button:
                mContext.editTextPassword.setText("");
                isLogin = !isLogin;
                if (!isLogin) {
                    mContext.buttonLogin.setText("Register");
                    mContext.buttonRegister.setText("Already have an account? Login");
                } else {
                    mContext.buttonLogin.setText("Login");
                    mContext.buttonRegister.setText("Register");
                }
                break;
        }
    }

    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(500);
        return translateAnimation;
    }

}

