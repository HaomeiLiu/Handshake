package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
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
    private String EventId;
    private long EventRoomId;
    private ImageView imageViewEvent;
    private Button buttonJoin;

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
        EventRoomId = Long.parseLong(EventId, 10);

        textViewTitle.setText(event.getTitle());
        textViewTime.setText("Time: "+event.getTime());
        textViewLocation.setText("Location: "+event.getLocation());
        textViewParticipants.setText("Participants: " + event.getParticipants());
        textViewDetail.setText("Key Words: "+event.getKey());

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass in information and start a chat
                Conversation.createChatRoomConversation(EventRoomId);
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
