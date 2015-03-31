package pl.edu.agh.dsrg.sr.chat.domain.channel;

import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.receiver.ChatChannelReceiver;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelFactory {

    private final String nickName;

    public ChannelFactory(String nickName) {
        this.nickName = nickName;
    }

    public ChatChannel create(ChannelName channelName) {
        JChannel jChannel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        jChannel.setProtocolStack(stack);
        ChatConfig.buildProtocolStack(stack, channelName);

        jChannel.setReceiver(new ChatChannelReceiver(jChannel, nickName, channelName));
        return new ChatChannel(channelName, jChannel);
    }
}
