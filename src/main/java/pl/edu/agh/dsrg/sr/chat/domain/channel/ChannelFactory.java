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
    private final ChatChannelRepository channelRepository;

    public ChannelFactory(String nickName, ChatChannelRepository channelRepository) {
        this.nickName = nickName;
        this.channelRepository = channelRepository;
    }

    public ChatChannel create(ChannelName channelName) {
        JChannel jChannel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        jChannel.setProtocolStack(stack);
        ChatConfig.buildProtocolStack(stack, channelName);

        jChannel.setReceiver(new ChatChannelReceiver(jChannel, nickName, channelName, channelRepository));
        return new ChatChannel(channelName, jChannel);
    }
}
