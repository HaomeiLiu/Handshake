package itp341.liu.haomei.finalprojecthaomeiliu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class WelcomeActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);

        mContext = this;
        initData();
    }

    private void initData() {
        //check if the account is logged in
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(mContext, HomeActivity.class));
            finish();
        }

    }


}
