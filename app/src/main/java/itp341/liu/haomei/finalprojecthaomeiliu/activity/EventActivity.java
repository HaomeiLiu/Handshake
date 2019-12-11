package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.im.UserChatActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.model.Event;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ToastUtil;

import static itp341.liu.haomei.finalprojecthaomeiliu.activity.HomeFragment.EXTRA_EVENT;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";
    private Event event;
    private TextView textViewTitle;
    private TextView textViewTime;
    private TextView textViewLocation;
    private TextView textViewParticipants;
    private TextView textViewDetail;
    private long EventId;
    private ImageView imageViewEvent;
    private Button buttonJoin;
    private static final long TEST_CHAT_ID = 23643144;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        setTitle("Event Detail");

        Intent intent = getIntent();
        if(intent != null){
            event = (Event) intent.getSerializableExtra(EXTRA_EVENT);
        }
        else{
            ToastUtil.shortToast(this, "Error loading event.");
            finishActivity(RESULT_CANCELED);
        }

        textViewTitle = findViewById(R.id.event_tv_name);
        textViewTime= findViewById(R.id.event_tv_time);
        textViewLocation = findViewById(R.id.event_tv_location);
        textViewParticipants = findViewById(R.id.event_tv_participants);
        textViewDetail = findViewById(R.id.event_tv_datail);
        imageViewEvent = findViewById(R.id.event_iv);
        buttonJoin = findViewById(R.id.event_button_join);

        EventId = event.getId();
        Log.d(TAG, "Event id "+EventId);


        textViewTitle.setText(event.getTitle());
        textViewTime.setText("Time: "+event.getTime());
        textViewLocation.setText("Location: "+event.getLocation());
        textViewParticipants.setText("Participants: " + event.getParticipants());
        textViewDetail.setText("Key Words: "+event.getKey());

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass in information and start a chat
//                int oldPart = event.getParticipants();
//                event.setParticipants(oldPart++);
                Conversation conv = Conversation.createChatRoomConversation(TEST_CHAT_ID);
                JMessageClient.getGroupMembers(TEST_CHAT_ID, new RequestCallback<List<GroupMemberInfo>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                        if(groupMemberInfos != null){
                            event.setParticipants(groupMemberInfos.size());
                        }
                    }
                });
                Intent intent = new Intent(EventActivity.this, UserChatActivity.class);
                intent.putExtra(JGApplication.CONV_TYPE, ConversationType.chatroom);
                intent.putExtra(EXTRA_EVENT, event);
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
