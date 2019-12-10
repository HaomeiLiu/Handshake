package itp341.liu.haomei.finalprojecthaomeiliu.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SharePreferenceManager;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.util.SortTopConvList;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ThreadUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.util.TimeFormat;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatListView;

public class PMListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Conversation> data;
    private ChatListView chatListView;

    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3003;
    private UserInfo mUserInfo;
    private GroupInfo mGroupInfo;

    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();


    public PMListAdapter(Activity context, List<Conversation> data, ChatListView chatListView) {
        this.mContext = context;
        this.data = data;
        this.chatListView = chatListView;
    }

    /**
     * Put the message on top when received a message
     * Referenced from JChat App
     * @param conv conversation to be put at top
     */
    public void setToTop(Conversation conv) {
        int oldCount = 0;
        int newCount = 0;
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {
                chatListView.setNullConversation(true);
            }
        });

        //If conversation is old, compare the conversation with other
        for (Conversation conversation : data) {
            if (conv.getId().equals(conversation.getId())) {
                //Remove before rearrangement
                data.remove(conversation);
                //Rearrangement
                //Rule: compare each conversation's incoming time with other conversation.
                for (int i = data.size(); i > SharePreferenceManager.getCancelTopSize(); i--) {
                    if (conv.getLatestMessage() != null && data.get(i - 1).getLatestMessage() != null) {
                        if (conv.getLatestMessage().getCreateTime() > data.get(i - 1).getLatestMessage().getCreateTime()) {
                            oldCount = i - 1;
                        } else {
                            oldCount = i;
                            break;
                        }
                    } else {
                        oldCount = i;
                    }
                }
                data.add(oldCount, conv);
                mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                return;
            }
        }
        if (data.size() == 0) {
            data.add(conv);
        } else {
            //If it is a new conversation, put it on top
            for (int i = data.size(); i > SharePreferenceManager.getCancelTopSize(); i--) {
                if (conv.getLatestMessage() != null && data.get(i - 1).getLatestMessage() != null) {
                    if (conv.getLatestMessage().getCreateTime() > data.get(i - 1).getLatestMessage().getCreateTime()) {
                        newCount = i - 1;
                    } else {
                        newCount = i;
                        break;
                    }
                } else {
                    newCount = i;
                }
            }
            data.add(newCount, conv);
        }
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }


    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Conversation getItem(int position) {
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Conversation convItem = data.get(position);
        //chatListView.setUnReadMsg(JMessageClient.getAllUnReadMsgCount());
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_chat, null);
        }
        final ImageView imageViewAvatar = convertView.findViewById(R.id.chatlist_iv_head);
        TextView textViewConName = convertView.findViewById(R.id.chatlist_tv_username);
        TextView textViewContent = convertView.findViewById(R.id.chatlist_tv_content);
        TextView textViewDate = convertView.findViewById(R.id.chatlist_tv_date);
        TextView textViewNewGroupNum = convertView.findViewById(R.id.chatlist_new_group_msg_number);
        TextView textViewNewMesNum = convertView.findViewById(R.id.chatlist_new_msg_number);
        ImageView imageViewFailed = convertView.findViewById(R.id.chatlist_iv_send_failed);

        Message lastMsg = convItem.getLatestMessage();
        if (lastMsg != null) {
            TimeFormat timeFormat = new TimeFormat(mContext, lastMsg.getCreateTime());
            textViewDate.setText(timeFormat.getTime());
            String content = ((TextContent) lastMsg.getContent()).getText();

            if (lastMsg.getStatus() == MessageStatus.send_fail) {
                imageViewFailed.setVisibility(View.VISIBLE);
            }else {
                imageViewFailed.setVisibility(View.GONE);
            }

            MessageContent msgContent = lastMsg.getContent();
            Boolean isRead = msgContent.getBooleanExtra("isRead");

            textViewContent.setText(content);
        }
        else{
            if (convItem.getLastMsgDate() == 0) {
                //Show last message
                textViewDate.setText("");
                textViewContent.setText("");
            } else {
                TimeFormat timeFormat = new TimeFormat(mContext, convItem.getLastMsgDate());
                textViewDate.setText(timeFormat.getTime());
                textViewContent.setText("");
            }
        }

        //Set chat name and icon
        if (convItem.getType().equals(ConversationType.single)) {
            if(convItem.getUnReadMsgCnt() == 0){
                textViewNewMesNum.setVisibility(View.GONE);
            }
            textViewConName.setText(convItem.getTitle());
            mUserInfo = (UserInfo) convItem.getTargetInfo();
            if (mUserInfo != null) {
                mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            imageViewAvatar.setImageBitmap(bitmap);
                        } else {
                            imageViewAvatar.setImageResource(R.drawable.user_blank_avatar);
                        }
                    }
                });
            } else {
                imageViewAvatar.setImageResource(R.drawable.user_blank_avatar);
            }
        }


//Set new message count and visibility
        if (convItem.getUnReadMsgCnt() > 0) {
            textViewNewGroupNum.setVisibility(View.GONE);
            textViewNewMesNum.setVisibility(View.GONE);
            if (convItem.getType().equals(ConversationType.single)) {
                textViewNewMesNum.setVisibility(View.VISIBLE);
                if (convItem.getUnReadMsgCnt() < 100) {
                    textViewNewMesNum.setText(String.valueOf(convItem.getUnReadMsgCnt()));
                } else {
                    textViewNewMesNum.setText("99+");
                }
            }

        }
        return convertView;

    }


    private static class UIHandler extends Handler {
//cleared by the Garbage Collector when it's weakly reachable.
        private final WeakReference<PMListAdapter> mAdapter;

        public UIHandler(PMListAdapter adapter) {
            mAdapter = new WeakReference<>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            PMListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    public void sortConvList() {
        forCurrent.clear();
        topConv.clear();
        int i = 0;
        SortConvList sortConvList = new SortConvList();
        Collections.sort(data, sortConvList);
        for (Conversation con : data) {
            if (!TextUtils.isEmpty(con.getExtra())) {
                forCurrent.add(con);
            }
        }
        topConv.addAll(forCurrent);
        data.removeAll(forCurrent);
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                data.add(i, conv);
                i++;
            }
        }
        notifyDataSetChanged();
    }

    public void addNewConversation(Conversation conv) {
        data.add(0, conv);
        if (data.size() > 0) {
            chatListView.setNullConversation(true);
        } else {
            chatListView.setNullConversation(false);
        }
        notifyDataSetChanged();
    }

    public void addAndSort(Conversation conv) {
        data.add(conv);
        SortConvList sortConvList = new SortConvList();
        Collections.sort(data, sortConvList);
        notifyDataSetChanged();
    }

    public void deleteConversation(Conversation conversation) {
        data.remove(conversation);
        notifyDataSetChanged();
    }

}
