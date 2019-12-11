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
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
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
        mContext = this;

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setTitle("Register");

        initView();
    }

    private void initView(){
        editText = findViewById(R.id.register_editText);
        buttonDone = findViewById(R.id.register_button);
        buttonBack = findViewById(R.id.return_btn);
        SharePreferenceManager.setCachedFixProfileFlag(true);

        buttonDone.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.register_button:
                    final ViewDialog dialog = new ViewDialog(mContext);
                    dialog.showDialog();

                    final String userId = SharePreferenceManager.getRegistrName();
                    Log.d("Register Activity", "username"+userId);
                    final String password = SharePreferenceManager.getRegistrPass();
                    SharePreferenceManager.setRegisterUsername(userId);
                    JMessageClient.login(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.hideDialog();
                            if (responseCode == 0) {
                                Log.d("Register Activity", "ResponseCode == 0");
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
                            else{
                                ToastUtil.shortToast(mContext, "Something went wrong...");
                            }
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
