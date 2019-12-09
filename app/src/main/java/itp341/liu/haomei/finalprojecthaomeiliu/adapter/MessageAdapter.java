package itp341.liu.haomei.finalprojecthaomeiliu.adapter;
//This code references https://www.scaledrone.com/blog/android-chat-tutorial/

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;

public class MessageAdapter extends BaseAdapter {
    List<Message> messages = new ArrayList<>();
    Context mContext;
    private Conversation conversation;
    private long roomID;
    private boolean isChatRoom = false;  //Default to user chat

    public MessageAdapter(Context context, Conversation conv) {
        this.mContext = context;
        this.conversation = conv;

        if (conv.getType() == ConversationType.single) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        } else{
            isChatRoom = true;
        }
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.getFromUser()== JMessageClient.getMyInfo()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.toString());
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.other_message, null);
            holder.avatar = convertView.findViewById(R.id.other_message_avatar);
            holder.name = convertView.findViewById(R.id.other_massage_name);
            holder.messageBody = convertView.findViewById(R.id.other_message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getFromUser().getNickname());
            holder.messageBody.setText(message.toString());
            if (!TextUtils.isEmpty(message.getFromUser().getAvatar())) {
                message.getFromUser().getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            notifyDataSetChanged();
                        }
                    }
                });
            }

        }

        return convertView;
    }

    class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
    }


}
