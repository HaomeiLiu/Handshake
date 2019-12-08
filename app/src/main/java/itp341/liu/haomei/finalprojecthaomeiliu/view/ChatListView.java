package itp341.liu.haomei.finalprojecthaomeiliu.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.activity.ChatFragment;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ThreadUtil;

public class ChatListView {
    private Context mContext;
    private View chatListFragment;
    private ListView pmListView;
    private ChatFragment chatFragment;
    private TextView nullConversation;


    public ChatListView(View view, Context context, ChatFragment fragment) {
        this.chatListFragment = view;
        this.mContext = context;
        this.chatFragment = fragment;
    }

    public void initModule() {
        pmListView = chatListFragment.findViewById(R.id.chat_pm_list);
        nullConversation = chatListFragment.findViewById(R.id.null_conversation);
    }

    public void setChatListAdapter(ListAdapter adapter) {
        pmListView.setAdapter(adapter);
    }

    public void setItemListeners(AdapterView.OnItemClickListener onClickListener) {
        pmListView.setOnItemClickListener(onClickListener);
    }

    public void setNullConversation(boolean isNotEmpty) {
        if (isNotEmpty) {
            nullConversation.setVisibility(View.GONE);
        } else {
            nullConversation.setVisibility(View.VISIBLE);
        }
    }

    /*public void setUnReadMsg(final int count) {
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAllUnReadMsg != null) {
                    if (count > 0) {
                        mAllUnReadMsg.setVisibility(View.VISIBLE);
                        if (count < 100) {
                            mAllUnReadMsg.setText(count + "");
                        } else {
                            mAllUnReadMsg.setText("99+");
                        }
                    } else {
                        mAllUnReadMsg.setVisibility(View.GONE);
                    }
                }
            }
        });
    }*/
}
