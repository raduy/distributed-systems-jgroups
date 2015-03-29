package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.channel.User;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class MessageContext {
    private final User user;
    private final ChatChannel chatChannel;

    public MessageContext(User user, ChatChannel chatChannel) {
        this.user = user;
        this.chatChannel = chatChannel;
    }

    public User getUser() {
        return user;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }
}
