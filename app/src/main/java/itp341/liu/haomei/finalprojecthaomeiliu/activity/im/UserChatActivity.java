package itp341.liu.haomei.finalprojecthaomeiliu.activity.im;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ChatRoomMessageEvent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.map.MapActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.entity.EventType;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ViewDialog;
import itp341.liu.haomei.finalprojecthaomeiliu.view.listview.DropDownListView;

import static itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter.EXTRA_USER_NAME;
import static itp341.liu.haomei.finalprojecthaomeiliu.controller.ChatRoomController.EXTRA_CHAT_ID;
import static itp341.liu.haomei.finalprojecthaomeiliu.controller.ChatRoomController.EXTRA_CHAT_NAME;

public class UserChatActivity extends AppCompatActivity {
    private Activity mContext;
    private EditText editText;
    private ImageButton buttonSend;
    private ImageButton buttonLocation;
    private static final String TAG = "UserChatActivity";
    private long roomID;
    private String targetID;
    public ListView listViewMessage;
    private MessageAdapter messageAdapter;
    private static final long TEST_CHAT_ID = 23643144;
    private boolean isPrivate = false;
    private Conversation mConv;
    public static final int REQUEST_CODE_MAP = 103;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mContext = this;
        JMessageClient.registerEventReceiver(this);


        messageAdapter = new MessageAdapter(mContext,JMessageClient.getChatRoomConversation(TEST_CHAT_ID));

        editText = findViewById(R.id.user_chat_editText);
        buttonSend = findViewById(R.id.user_chat_button);
        listViewMessage = findViewById(R.id.user_chat_listView);
        buttonLocation = findViewById(R.id.user_chat_send_location);
        listViewMessage.setAdapter(messageAdapter);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //For Event Chat
        ConversationType conversationType = (ConversationType) getIntent().getSerializableExtra(JGApplication.CONV_TYPE);
        Intent intent = getIntent();
        if(conversationType == ConversationType.chatroom){
            buttonLocation.setVisibility(View.GONE);
            buttonLocation.setClickable(false);
            Event event;
            if(intent != null){
                event = (Event) intent.getSerializableExtra(HomeFragment.EXTRA_EVENT);
                if(event != null){
                    //The intent is from EventActivity, which passes in an event object
                    setTitle(event.getTitle());
                    roomID = TEST_CHAT_ID;
                    initChatRoom(roomID);
                }
                else{
                    //The intent is from ChatFragment->ChatRoomController, which passes in chatRoomId and chatRoomName
                    setTitle(intent.getStringExtra(EXTRA_CHAT_NAME));
                    roomID = intent.getLongExtra(EXTRA_CHAT_ID, -1);
                }

            }
        }
        else{
            //Private Chat
            buttonLocation.setVisibility(View.VISIBLE);
            buttonLocation.setClickable(true);
            isPrivate = true;
            if(intent != null){
                targetID = intent.getStringExtra(EXTRA_USER_NAME);
                setTitle(targetID);
                Conversation conv = JMessageClient.getSingleConversation(targetID);
                mConv = conv;
                conv.setUnReadMessageCnt(0);
                messageAdapter.addMsgListToList(JMessageClient.getSingleConversation(targetID).getAllMessage());
            }
            else{
                finish();
            }

        }

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(UserChatActivity.this, MapActivity.class);
                intent2.putExtra(EXTRA_USER_NAME, targetID);
                startActivityForResult(intent2, REQUEST_CODE_MAP);
            }
        });

        //Send a message
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");
                if(editText.getText().toString().equals("")){
                    //Do nothing
                }
                else{
                    if(!isPrivate){
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
                    else{
                        Conversation conv = JMessageClient.getSingleConversation(targetID);
                        if (null == conv) {
                            conv = Conversation.createSingleConversation(targetID);
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
                    editText.clearAnimation();
                    editText.setText("");
                }

            }
        });

    }



    private void initChatRoom(final long chatRoomId) {
        final ViewDialog dialog = new ViewDialog(mContext);
        dialog.showDialog();
        //Enter Chatroom
        ChatRoomManager.enterChatRoom(chatRoomId, new RequestCallback<Conversation>() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Conversation conversation) {
                if(responseCode == 0){
                    //Something unexpected happened
                    dialog.hideDialog();
                    ToastUtil.shortToast(mContext, "Failed to enter the event.");
                    finish();
                }
                if(conversation == null){
                    mConv = Conversation.createChatRoomConversation(chatRoomId);
                }
                else{
                    mConv = conversation;
                }
                messageAdapter.addMsgListToList(conversation.getAllMessage());
                //Proceed successfully
                dialog.hideDialog();
                String result = null != conversation ? conversation.toString() : null;
                Log.d(TAG, "result: "+result);

            }
        });
    }

    @Subscribe (threadMode = ThreadMode.MAIN)

    public void onEvent(CommandNotificationEvent event) {
        if (event.getType().equals(CommandNotificationEvent.Type.single)) {
            final String msg = event.getMsg();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(msg);
                        JSONObject jsonContent = object.getJSONObject("content");
                        String messageString = jsonContent.getString("message");
                        if (TextUtils.isEmpty(messageString)) {
                            setTitle(mConv.getTitle());
                        } else {
                            setTitle(messageString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onEventMainThread(ChatRoomMessageEvent event) {
        Log.d("UserChatActivity", "onEventMainThread triggered!");
        List<Message> messages = event.getMessages();
        messageAdapter.addMsgListToList(messages);
    }

    //Receiver for private chat
    public void onEvent(MessageEvent event) {
        Log.d("UserChatActivity", "MessageEvent triggered!");
        final Message msg = event.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msg.getTargetType() == ConversationType.single) {
                    UserInfo userInfo = (UserInfo) msg.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    if (isPrivate && targetId.equals(targetID)) {
                        Message lastMsg = messageAdapter.getLastMsg();
                        if (lastMsg == null || msg.getId() != lastMsg.getId()) {
                            messageAdapter.add(msg);
                        } else {
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void onEvent(OfflineMessageEvent event) {
        Log.d("UserChatActivity", "OfflineMessageEvent triggered!");
        Conversation conv = event.getConversation();
        if (conv.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            String new_targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (isPrivate && new_targetId.equals(targetID)) {
                List<Message> singleOfflineMsgList = event.getOfflineMessageList();
                if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                    messageAdapter.addMsgListToList(singleOfflineMsgList);
                }
            }
        }
    }

    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        List<MessageReceiptStatusChangeEvent.MessageReceiptMeta> messageReceiptMetas = event.getMessageReceiptMetas();
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : messageReceiptMetas) {
            long serverMsgId = meta.getServerMsgId();
            int unReceiptCnt = meta.getUnReceiptCnt();
            messageAdapter.setUpdateReceiptCount(serverMsgId, unReceiptCnt);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        JMessageClient.exitConversation();
        JGApplication.delConversation = null;
        if (mConv.getAllMessage() == null || mConv.getAllMessage().size() == 0) {
            if (isPrivate) {
                JMessageClient.deleteSingleConversation(targetID);
            }
            JGApplication.delConversation = mConv;
            if(!isPrivate){
                ChatRoomManager.leaveChatRoom(roomID, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        UserChatActivity.this.finish();
                        UserChatActivity.super.onBackPressed();
                    }
                });
            }
            else{
                finish();
                super.onBackPressed();
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
    }

    @Override
    protected void onResume() {
        if(isPrivate){
            String targetId = getIntent().getStringExtra(EXTRA_USER_NAME);
            if (null != targetId) {
                JMessageClient.enterSingleConversation(targetId);
            }
        }

        //Delete in history, then go back to chat view, refresh
        if (JGApplication.ids != null && JGApplication.ids.size() > 0) {
            for (Message msg : JGApplication.ids) {
                messageAdapter.removeMessage(msg);
            }
        }
        if (messageAdapter != null)
            messageAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MAP){
            Log.d("UserChatActivity", "Inside request code");
            String text = "[Location shared to you]";
            Conversation conv = JMessageClient.getSingleConversation(targetID);
            if (null == conv) {
                conv = Conversation.createSingleConversation(targetID);
            }
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
    }
}

