package itp341.liu.haomei.finalprojecthaomeiliu.controller;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.ChatActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.ChatFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.im.UserChatActivity;
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.PMListAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortTopConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatListView;

import static itp341.liu.haomei.finalprojecthaomeiliu.adapter.MessageAdapter.EXTRA_USER_NAME;

public class PMListController implements AdapterView.OnItemClickListener{

    private ChatListView chatListView;
    private ChatFragment mContext;
    private PMListAdapter pmListAdapter;
    private List<Conversation> data = new ArrayList<Conversation>();
    private Dialog dialog;
    private int width;
    private static final int REQUEST_CODE_PM = 101;

    public PMListController(ChatListView chatListView, ChatFragment context){
        this.chatListView = chatListView;
        this.mContext = context;
        initPMListAdapter();
    }

    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();
    List<Conversation> delFeedBack = new ArrayList<>();
    private void initPMListAdapter(){
        forCurrent.clear();
        topConv.clear();
        delFeedBack.clear();
        int i = 0;
        data = JMessageClient.getConversationList();
        if (data != null && data.size() > 0) {
            chatListView.setNullConversation(true);
            SortConvList sortConvList = new SortConvList();
            Collections.sort(data, sortConvList);
            for (Conversation con : data) {
                //If the conversation already exists, delete it
                if (con.getTargetId().equals("feedback_Android") || con.getType().equals(ConversationType.chatroom)) {
                    delFeedBack.add(con);
                }
                if (!TextUtils.isEmpty(con.getExtra())) {
                    forCurrent.add(con);
                }
            }
            topConv.addAll(forCurrent);
            data.removeAll(forCurrent);
            data.removeAll(delFeedBack);

        } else {
            chatListView.setNullConversation(false);
        }
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                data.add(i, conv);
                i++;
            }
        }
        pmListAdapter = new PMListAdapter(mContext.getActivity(), data, chatListView);
        chatListView.setChatListAdapter(pmListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        if (position >= 0) {
            Conversation conv = data.get(position);
            String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
            intent.putExtra(EXTRA_USER_NAME, targetId);
            intent.setClass(mContext.getActivity(), UserChatActivity.class);
            mContext.getActivity().startActivityForResult(intent, REQUEST_CODE_PM);

        }
    }

    public PMListAdapter getAdapter() {
        return pmListAdapter;
    }



}
