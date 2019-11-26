package itp341.liu.haomei.finalprojecthaomeiliu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import itp341.liu.haomei.finalprojecthaomeiliu.controller.LoginController;
import itp341.liu.haomei.finalprojecthaomeiliu.im.BaseActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;

public class LoginActivity extends BaseActivity {
    private CallbackManager callbackManager;
    private Button buttonFacebook;
    public Button buttonRegister;
    public Button buttonLogin;
    public EditText editTextUser;
    public EditText editTextPassword;
    private LoginController loginController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
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
//        if(isLoggedIn){
//            goToActivity(LoginActivity.this, HomeActivity.class);
//        }

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
