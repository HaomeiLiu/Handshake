package itp341.liu.haomei.finalprojecthaomeiliu.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.controller.ChatRoomController;
import itp341.liu.haomei.finalprojecthaomeiliu.controller.PMListController;
import itp341.liu.haomei.finalprojecthaomeiliu.entity.EventJM;
import itp341.liu.haomei.finalprojecthaomeiliu.util.ThreadUtil;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatListView;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatRoomView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChatFragment extends Fragment {

    private Activity mContext;
    private ChatListView chatListView;
    private PMListController pmListController;
    private View mRootView;
    private HandlerThread mThread;
    private NetworkReceiver mReceiver;
    private BackgroundHandler mBackgroundHandler;

    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private static final int DISMISS_REFRESH_HEADER = 0x3001;
    private static final int ROAM_COMPLETED = 0x3002;

    protected boolean isCreate = false;

    //chatroom
    private ChatRoomView mChatRoomView;
    private ChatRoomController mRoomController;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        isCreate = true;
        mContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }

        chatListView = new ChatListView(mRootView, this.getActivity(), this);
        chatListView.initModule();

        mChatRoomView = mRootView.findViewById(R.id.chat_room_view);
        mChatRoomView.initModule();
        mRoomController = new ChatRoomController(mChatRoomView, mContext);
        mChatRoomView.setListener(mRoomController);

        mThread = new HandlerThread("ChatFragment");
        mThread.start();

        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());
        pmListController = new PMListController(chatListView, this);
        chatListView.setItemListeners(pmListController);

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null ) {
            mBackgroundHandler.sendEmptyMessageDelayed(DISMISS_REFRESH_HEADER, 1000);
        }
        initReceiver();


        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            //mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy(){
        EventBus.getDefault().unregister(this);
        mContext.unregisterReceiver(mReceiver);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pmListController.getAdapter().notifyDataSetChanged();
        mRoomController.getChatRoomAdapter().notifyDataSetChanged();


    }

    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(mReceiver, filter);
    }

    //Broadcast that listens to network connectivity
    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                    //Network not working properly
                }
            }
        }
    }

    /**
     * Receive message
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
        String targetId = userInfo.getUserName();
        Conversation conv = JMessageClient.getSingleConversation(targetId, userInfo.getAppKey());
        if (conv != null && pmListController != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(userInfo.getAvatar())) {
                        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage, Bitmap avatarBitmap) {
                                if (responseCode == 0) {
                                    pmListController.getAdapter().notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }
    }

    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }
    }

    /**
     * Message read
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        pmListController.getAdapter().notifyDataSetChanged();
    }

    /**
     * Roam
     */
    public void onEvent(ConversationRefreshEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            //Multiple clients -> not applicable
            if (event.getReason().equals(ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED)) {
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            }
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    if (conv.getType() != ConversationType.chatroom) {
                        pmListController.getAdapter().setToTop(conv);
                    }
                    break;
                case DISMISS_REFRESH_HEADER:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    break;
                case ROAM_COMPLETED:
                    conv = (Conversation) msg.obj;
                    pmListController.getAdapter().addAndSort(conv);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventJM event) { ;
        switch (event.getType()) {
            case createConversation:
                Conversation conv = event.getConversation();
                if (conv != null) {
                    pmListController.getAdapter().addNewConversation(conv);
                }
                break;
            case deleteConversation:
                conv = event.getConversation();
                if (null != conv) {
                    pmListController.getAdapter().deleteConversation(conv);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pmListController.getAdapter() != null){
            pmListController.getAdapter().notifyDataSetChanged();
        }
        if(mRoomController.getChatRoomAdapter() != null){
            mRoomController.getChatRoomAdapter().notifyDataSetChanged();
        }
    }
}
