package itp341.liu.haomei.finalprojecthaomeiliu.entity;

import cn.jpush.im.android.api.model.Conversation;

public class EventJM {

    private EventType type;
    private Conversation conversation;

    public EventJM(EventType type, Conversation conv) {
        this.type = type;
        this.conversation = conv;
    }

    public EventType getType() {
        return type;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public static class Builder {
        private EventType type;
        private Conversation conversation;

        public Builder setType(EventType type) {
            this.type = type;
            return this;
        }

        public Builder setConversation(Conversation conv) {
            this.conversation = conv;
            return this;
        }

        public EventJM build() {
            return new EventJM(type, conversation);
        }

    }

}
