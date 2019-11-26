package itp341.liu.haomei.finalprojecthaomeiliu.entity;

import android.content.Context;
import android.content.Intent;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import itp341.liu.haomei.finalprojecthaomeiliu.application.JGApplication;
import itp341.liu.haomei.finalprojecthaomeiliu.im.ChatActivity;

public class NotificationClickEventReceiver {
    private Context mContext;

    public NotificationClickEventReceiver(Context context) {
        mContext = context;
        //Register for event receiver
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * manage notification
     *
     * @param notificationClickEvent
     */
    public void onEvent(NotificationClickEvent notificationClickEvent) {
        if (null == notificationClickEvent) {
            return;
        }
        Message msg = notificationClickEvent.getMessage();
        if (msg != null) {
            String targetId = msg.getTargetID();
            String appKey = msg.getFromAppKey();
            ConversationType type = msg.getTargetType();
            Conversation conv;
            Intent notificationIntent = new Intent(mContext, ChatActivity.class);
            if (type == ConversationType.single) {
                conv = JMessageClient.getSingleConversation(targetId, appKey);
                notificationIntent.putExtra(JGApplication.TARGET_ID, targetId);
                notificationIntent.putExtra(JGApplication.TARGET_APP_KEY, appKey);
            } else {
                conv = JMessageClient.getGroupConversation(Long.parseLong(targetId));
                notificationIntent.putExtra(JGApplication.GROUP_ID, Long.parseLong(targetId));
            }
            notificationIntent.putExtra(JGApplication.CONV_TITLE, conv.getTitle());
            conv.resetUnreadCount();
//        notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.putExtra("fromGroup", false);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(notificationIntent);
        }
    }
}