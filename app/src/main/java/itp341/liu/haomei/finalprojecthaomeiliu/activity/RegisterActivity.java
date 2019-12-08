package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.db.UserEntry;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.im.BaseActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;

public class RegisterActivity extends BaseActivity {

    private EditText editText;
    private Button buttonDone;
    private ImageButton buttonBack;
    private RegisterActivity mContext;

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

        buttonBack.setOnClickListener(listener);
        buttonDone.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                case R.id.register_button:
                    final ViewDialog dialog = new ViewDialog(mContext);
                    dialog.showDialog();

                    final String userId = SharePreferenceManager.getRegistrName();
                    final String password = SharePreferenceManager.getRegistrPass();
                    SharePreferenceManager.setRegisterUsername(userId);
                    JMessageClient.login(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                Log.d("Register Activity", "ResponseCode == 0");
                                dialog.hideDialog();
                                JGApplication.isLogin = true;
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
