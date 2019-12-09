package itp341.liu.haomei.finalprojecthaomeiliu.activity.im;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.scaledrone.lib.Scaledrone;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;
import itp341.liu.haomei.finalprojecthaomeiliu.view.listview.DropDownListView;

public class UserChatActivity extends AppCompatActivity {
    private Activity mContext;
    private EditText editText;
    private ImageButton buttonSend;
    private static final String TAG = "UserChatActivity";
    private long roomID;
    public ListView listViewMessage;
    private MessageAdapter messageAdapter;
    private static final long TEST_CHAT_ID = 23643144;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        mContext = this;

        messageAdapter = new MessageAdapter(mContext,JMessageClient.getChatRoomConversation(TEST_CHAT_ID));

        editText = findViewById(R.id.user_chat_editText);
        buttonSend = findViewById(R.id.user_chat_button);
        listViewMessage = findViewById(R.id.user_chat_listView);
        listViewMessage.setAdapter(messageAdapter);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        //For Event Chat
        ConversationType conversationType = (ConversationType) getIntent().getSerializableExtra(JGApplication.CONV_TYPE);
        Intent intent = getIntent();
        if(conversationType == ConversationType.chatroom){
            Event event;
            if(intent != null){
                event = (Event) intent.getSerializableExtra(HomeFragment.EXTRA_EVENT);
                setTitle(event.getTitle());
//                roomID = event.getId();
                roomID = TEST_CHAT_ID;
                initChatRoom(roomID);
            }
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");
                Conversation conv = JMessageClient.getChatRoomConversation(roomID);
                if (null == conv) {
                    conv = Conversation.createChatRoomConversation(roomID);
                }
                String text = editText.getText().toString();
                final Message msg = conv.createSendTextMessage(text);
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        if (0 == responseCode) {
                            messageAdapter.add(msg);
                        } else {
                            ToastUtil.shortToast(mContext, "Unable to send at this time");
                        }
                    }
                });
                JMessageClient.sendMessage(msg);

            }
        });

    }



    private void initChatRoom(long chatRoomId) {
        final ViewDialog dialog = new ViewDialog(mContext);
        dialog.showDialog();
        //Enter Chatroom
        ChatRoomManager.enterChatRoom(chatRoomId, new RequestCallback<Conversation>() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Conversation conversation) {
                /*if(responseCode != 0){
                    //Something unexpected happened
                    dialog.hideDialog();
                    ToastUtil.shortToast(mContext, "Failed to enter the event.");
                    finish();
                }*/
                //Proceed successfully
                dialog.hideDialog();
                String result = null != conversation ? conversation.toString() : null;

            }
        });

    }
    public void onEventMainThread(ChatRoomMessageEvent event) {
        Log.d(TAG, "ChatRoomMessageEvent received .");
        List<Message> msgs = event.getMessages();
        for (Message msg : msgs) {
            //这个页面仅仅展示聊天室会话的消息
            messageAdapter.add(msg);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

