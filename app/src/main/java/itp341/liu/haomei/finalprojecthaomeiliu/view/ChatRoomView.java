package itp341.liu.haomei.finalprojecthaomeiliu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.ChatFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.ChatRoomAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.controller.ChatRoomController;

public class ChatRoomView extends LinearLayout {
    private ChatRoomController mListener;
    private Context mContext;
    private ListView mChatRoomListView;
    private LayoutInflater mInflater;
    private LinearLayout mSearch_title;
    private SmartRefreshLayout smartRefreshLayout;
    private TextView mNullChatRoom;

    public ChatRoomView(Context context) {
        super(context);
    }

    public ChatRoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    public void initModule() {
        mChatRoomListView = findViewById(R.id.chat_group_list);
        mNullChatRoom = findViewById(R.id.null_chatroom);
    }

    public void setListener(ChatRoomController listener) {
        mChatRoomListView.setOnItemClickListener(listener);
    }

    public void setChatRoomAdapter(ChatRoomAdapter chatRoomAdapter) {
        mChatRoomListView.setAdapter(chatRoomAdapter);
    }


    public void setNullChatRoom(boolean isNullChatRoom) {
        mNullChatRoom.setVisibility(isNullChatRoom ? VISIBLE : GONE);
    }

}
