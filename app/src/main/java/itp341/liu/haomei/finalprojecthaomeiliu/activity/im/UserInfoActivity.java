package itp341.liu.haomei.finalprojecthaomeiliu.activity.im;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;

import static itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter.EXTRA_USER_AVATAR;
import static itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter.EXTRA_USER_NAME;

public class UserInfoActivity extends AppCompatActivity {
    private UserInfo user;
    private TextView textViewName;
    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        final Intent intent = getIntent();
        if(intent != null){
            Log.d("UserInfoActivity", "EXTRA_USER_NAME"+intent.getStringExtra(EXTRA_USER_NAME));
            Log.d("UserInfoActivity", "Key"+SharePreferenceManager.getCachedAppKey());

            //Preferred way, but not working
            /*JMessageClient.getUserInfo(intent.getStringExtra(EXTRA_USER_NAME), new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    Log.d("UserInfoActivity", "inside got result");
                    if(i==0){
                        user = userInfo;
                        Log.d("UserInfoActivity", "info "+userInfo.toString());
                    }
                    else{
                        ToastUtil.shortToast(UserInfoActivity.this, "Unable to find the user");
                        Log.d("UserInfoActivity", "Unable to find the user");
                        finish();
                    }
                }
            });*/
            JMessageClient.getUserInfo(intent.getStringExtra(EXTRA_USER_NAME), new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    user = userInfo;
                }
            });
        }
        else{
            Log.d("UserInfoActivity", "No intent");
            finish();
        }

//        setTitle(user.getUserName());
        setTitle(intent.getStringExtra(EXTRA_USER_NAME));
        textViewName = findViewById(R.id.user_info_name);
        imageView = findViewById(R.id.user_info_iv);
        button = findViewById(R.id.user_info_button);

        textViewName.setText(intent.getStringExtra(EXTRA_USER_NAME));
        if (!TextUtils.isEmpty(intent.getStringExtra(EXTRA_USER_AVATAR))) {
            user.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int status, String desc, Bitmap bitmap) {
                    if (status == 0) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToChat = new Intent(UserInfoActivity.this, UserChatActivity.class);
                goToChat.putExtra(EXTRA_USER_NAME, intent.getStringExtra(EXTRA_USER_NAME));
                startActivity(goToChat);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
