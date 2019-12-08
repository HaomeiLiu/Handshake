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
import itp341.liu.haomei.finalprojecthaomeiliu.adapter.PMListAdapter;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortTopConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatListView;

public class PMListController implements AdapterView.OnItemClickListener{

    private ChatListView chatListView;
    private ChatFragment mContext;
    private PMListAdapter pmListAdapter;
    private List<Conversation> data = new ArrayList<Conversation>();
    private Dialog dialog;
    private int width;

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
                //如果会话有聊天室会话就把这会话删除
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
        //点击会话条目
        Intent intent = new Intent();
        if (position > 0) {
            //这里-3是减掉添加的三个headView
            Conversation conv = data.get(position - 3);
            intent.putExtra(JGApplication.CONV_TITLE, conv.getTitle());
            //群聊
            if (conv.getType() == ConversationType.group) {
                long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
                intent.putExtra(JGApplication.GROUP_ID, groupId);
                intent.setClass(mContext.getActivity(), ChatActivity.class);
                mContext.getActivity().startActivity(intent);
                return;
                //单聊
            } else if (conv.getType() == ConversationType.single) {
                String targetId = ((UserInfo) conv.getTargetInfo()).getUserName();
                intent.putExtra(JGApplication.TARGET_ID, targetId);
                intent.putExtra(JGApplication.TARGET_APP_KEY, conv.getTargetAppKey());
            }
            intent.setClass(mContext.getActivity(), ChatActivity.class);
            mContext.getContext().startActivity(intent);

        }
    }

    public PMListAdapter getAdapter() {
        return pmListAdapter;
    }



}
