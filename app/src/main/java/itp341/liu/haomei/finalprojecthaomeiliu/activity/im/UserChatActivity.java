package itp341.liu.haomei.finalprojecthaomeiliu.activity.im;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;
import itp341.liu.haomei.finalprojecthaomeiliu.view.listview.DropDownListView;

public class UserChatActivity extends AppCompatActivity {
    @BindView(R.id.lv_chat) DropDownListView lvChat;
    private Activity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        mContext = this;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //For Event Chat
        ConversationType conversationType = (ConversationType) getIntent().getSerializableExtra(JGApplication.CONV_TYPE);
        Intent intent = getIntent();
        if(conversationType == ConversationType.chatroom){
            Event event;
            if(intent != null){
                event = (Event) intent.getSerializableExtra(HomeFragment.EXTRA_EVENT);

            }
        }

    }

    private void initChatRoom(long chatRoomId) {
        final ViewDialog dialog = new ViewDialog(mContext);
        dialog.showDialog();
        //Enter Chatroom
        ChatRoomManager.enterChatRoom(chatRoomId, new RequestCallback<Conversation>() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Conversation conversation) {
                if(responseCode != 0){
                    //Something unexpected happened
                    dialog.hideDialog();
                    ToastUtil.shortToast(mContext, "Failed to enter the event.");
                    finish();
                }
                //Proceed successfully
                dialog.hideDialog();
                String result = null != conversation ? conversation.toString() : null;

            }
        });

    }
}
