package itp341.liu.haomei.finalprojecthaomeiliu;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.db.UserEntry;
import itp341.liu.haomei.finalprojecthaomeiliu.im.BaseActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.util.DialogCreator;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;

public class RegisterActivity extends BaseActivity {

    private EditText editText;
    private Button buttonDone;
    private ImageButton buttonBack;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView(){
        editText = findViewById(R.id.register_editText);
        buttonDone = findViewById(R.id.register_button);
        buttonBack = findViewById(R.id.return_btn);
        SharePreferenceManager.setCachedFixProfileFlag(true);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonBack.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                case R.id.register_button:
                    dialog = DialogCreator.createLoadingDialog(RegisterActivity.this,
                            "Loading");
                    dialog.setCancelable(false);
                    dialog.show();

                    final String userId = SharePreferenceManager.getRegistrName();
                    final String password = SharePreferenceManager.getRegistrPass();
                    SharePreferenceManager.setRegisterUsername(userId);
                    JMessageClient.login(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                JGApplication.registerOrLogin = 1;
                                String username = JMessageClient.getMyInfo().getUserName();
                                String appKey = JMessageClient.getMyInfo().getAppKey();
                                UserEntry user = UserEntry.getUser(username, appKey);
                                if (null == user) {
                                    user = new UserEntry(username, appKey);
                                    user.save();
                                }

                                String nickName = editText.getText().toString();

                                UserInfo myUserInfo = JMessageClient.getMyInfo();
                                if (myUserInfo != null) {
                                    myUserInfo.setNickname(nickName);
                                }
                                //Renew Nickname while registering
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(final int status, String desc) {
                                        //renew jump sign
                                        SharePreferenceManager.setCachedFixProfileFlag(false);
                                        dialog.dismiss();
                                        if (status == 0) {
                                            goToActivity(RegisterActivity.this, HomeActivity.class);
                                        }
                                    }
                                });
                            }
                        }
                    });
                    break;
            }
        }
    };
}
