package itp341.liu.haomei.finalprojecthaomeiliu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jpush.im.android.api.model.ChatRoomInfo;
import itp341.liu.haomei.finalprojecthaomeiliu.R;
import itp341.liu.haomei.finalprojecthaomeiliu.view.ChatRoomView;

public class ChatRoomAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChatRoomInfo> mChatRoomInfoList;
    private ChatRoomView mChatRoomView;
    private LayoutInflater mInflater;

    public ChatRoomAdapter(Context context, List<ChatRoomInfo> chatRoomData, ChatRoomView chatRoomView) {
        this.mContext = context;
        this.mChatRoomInfoList = chatRoomData;
        this.mChatRoomView = chatRoomView;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mChatRoomInfoList == null ? 0 : mChatRoomInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mChatRoomInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_list_chat, null);
            holder.iv_chatRoomAvatar = convertView.findViewById(R.id.chatlist_iv_head);
            holder.tv_chatRoomName = convertView.findViewById(R.id.chatlist_tv_username);
            holder.tv_chatRoomContent = convertView.findViewById(R.id.chatlist_tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatRoomInfo chatRoomInfo = mChatRoomInfoList.get(position);
        holder.tv_chatRoomName.setText(chatRoomInfo.getName());
        holder.tv_chatRoomContent.setText(chatRoomInfo.getDescription());
        holder.iv_chatRoomAvatar.setImageResource(R.drawable.blank_group_icon);


        return convertView;
    }

    class ViewHolder {
        ImageView iv_chatRoomAvatar;
        TextView tv_chatRoomName;
        TextView tv_chatRoomContent;
    }
}
