package itp341.liu.haomei.finalprojecthaomeiliu.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.ChatRoomInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.ChatActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.ChatRoomAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.util.DialogCreator;
import itp341.liu.haomei.finalprojecthaomeiliu.util.HandleResponseCode;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatRoomView;

public class ChatRoomController implements AdapterView.OnItemClickListener{
    private ChatRoomView mChatRoomView;
    private Context mContext;
    private static final int PAGE_COUNT = 15;
    private List<ChatRoomInfo> chatRoomInfos = new ArrayList<>();
    private ChatRoomAdapter chatRoomAdapter;

    public ChatRoomController(ChatRoomView chatRoomView, Context context) {
        this.mChatRoomView = chatRoomView;
        this.mContext = context;
        initChatRoomAdapter();
    }

    private void initChatRoomAdapter() {
        final Dialog loadingDialog = DialogCreator.createLoadingDialog(mContext, "Loading...");
        loadingDialog.show();
        ChatRoomManager.getChatRoomListByApp(0, PAGE_COUNT, new RequestCallback<List<ChatRoomInfo>>() {
            @Override
            public void gotResult(int i, String s, List<ChatRoomInfo> result) {
                loadingDialog.dismiss();
                if (i == 0) {
                    chatRoomInfos.addAll(result);
                } else {
                    HandleResponseCode.onHandle(mContext, i, false);
                }
                chatRoomAdapter = new ChatRoomAdapter(mContext, chatRoomInfos, mChatRoomView);
                mChatRoomView.setChatRoomAdapter(chatRoomAdapter);
                mChatRoomView.setNullChatRoom(chatRoomInfos.size() == 0);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAtPosition = parent.getItemAtPosition(position);
        if (itemAtPosition != null && itemAtPosition instanceof ChatRoomInfo) {
            ChatRoomInfo info = (ChatRoomInfo) itemAtPosition;
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra(JGApplication.CONV_TYPE, ConversationType.chatroom);
            intent.putExtra("chatRoomId", info.getRoomID());
            intent.putExtra("chatRoomName", info.getName());
            mContext.startActivity(intent);
        }
    }


}
