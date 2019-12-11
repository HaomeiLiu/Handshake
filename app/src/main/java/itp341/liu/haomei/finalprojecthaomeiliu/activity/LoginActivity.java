package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.activeandroid.ActiveAndroid;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.controller.LoginController;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.im.BaseActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.db.UserEntry;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;

public class LoginActivity extends BaseActivity {
    private CallbackManager callbackManager;
    private Button buttonFacebook;
    public Button buttonRegister;
    public Button buttonLogin;
    public EditText editTextUser;
    public EditText editTextPassword;
    private LoginController loginController;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mContext = this;

        //Create a callback for facebook API
        callbackManager = CallbackManager.Factory.create();

        //initialize activeandroid
        ActiveAndroid.initialize(this);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        final String username = loginResult.getAccessToken().getUserId();
                        Log.d("LoginActivity", "UserId"+username);
                        final String password = "111111";
                        Log.d("LoginActivity", "check");
                        JMessageClient.register(username, password, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    SharePreferenceManager.setRegisterName(username);
                                    Log.d("Register", "Username inside login"+username);
                                    SharePreferenceManager.setRegistePass(password);
//                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                                    startActivity(intent);
                                    final ViewDialog dialog = new ViewDialog(LoginActivity.this);
                                    dialog.showDialog();
                                    Log.d("LoginActivity", "After dialog created and shown");
                                    JMessageClient.login(username, password, new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseMessage) {
                                            dialog.hideDialog();

                                            if (responseCode == 0) {
                                                SharePreferenceManager.setCachedPsw(password);
                                                UserInfo myInfo = JMessageClient.getMyInfo();
                                                //Successfully logged in.
                                                /*String username = myInfo.getUserName();
                                                String appKey = myInfo.getAppKey();
                                                UserEntry user = UserEntry.getUser(username, appKey);
                                                if (null == user) {
                                                    user = new UserEntry(username, appKey);
                                                    user.save();
                                                }*/
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                ToastUtil.shortToast(mContext, "Login Success");
                                            } else {
                                                ToastUtil.shortToast(mContext, "Login Failed. " + responseMessage);
                                            }
                                        }
                                    });
                                } else {
                                    final ViewDialog dialog = new ViewDialog(LoginActivity.this);
                                    dialog.showDialog();
                                    Log.d("LoginActivity", "After dialog created and shown");
                                    JMessageClient.login(username, password, new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseMessage) {
                                            dialog.hideDialog();

                                            if (responseCode == 0) {
                                                SharePreferenceManager.setCachedPsw(password);
                                                //UserInfo myInfo = JMessageClient.getMyInfo();
                                                //Successfully logged in.
                                                /*String username = myInfo.getUserName();
                                                String appKey = myInfo.getAppKey();
                                                UserEntry user = UserEntry.getUser(username, appKey);
                                                if (null == user) {
                                                    user = new UserEntry(username, appKey);
                                                    user.save();
                                                }*/
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                ToastUtil.shortToast(mContext, "Login Success");
                                            } else {
                                                ToastUtil.shortToast(mContext, "Login Failed. " + responseMessage);
                                            }
                                        }
                                    });
                                    ToastUtil.shortToast(mContext, "Register Failed");
                                    loginResult.getAccessToken().isExpired();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            goToActivity(LoginActivity.this, HomeActivity.class);
        }

        loginController = new LoginController(this);
        buttonLogin.setOnClickListener(loginController);
        buttonRegister.setOnClickListener(loginController);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView(){
        buttonFacebook = findViewById(R.id.login_button);
        buttonRegister = findViewById(R.id.register_button);
        buttonLogin = findViewById(R.id.login);
        editTextUser = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);

        if (editTextUser.getText().length() == 0 || editTextPassword.getText().length() == 0) {
            buttonLogin.setEnabled(false);
        }

        //load account that was last logged in
        String userName = SharePreferenceManager.getCachedUsername();

        editTextUser.setText(userName);
        if (userName != null) {
            editTextUser.setSelection(userName.length());
        }

        editTextUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextUser.getText().length() == 0 || editTextPassword.getText().length() == 0) {
                    buttonLogin.setEnabled(false);
                } else {
                    buttonLogin.setEnabled(true);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextUser.getText().length() == 0 || editTextPassword.getText().length() == 0) {
                    buttonLogin.setEnabled(false);
                } else {
                    buttonLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public String getUserId() {
        return editTextUser.getText().toString().trim();
    }

    public String getPassword() {
        return editTextPassword.getText().toString().trim();
    }


}
