package pl.edu.agh.dsrg.sr.chat.channel;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.command.ManagementChannelReceiver;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelsHandler {
    private static final String MANAGEMENT_CHANNEL_NAME = ChatConfig.MANAGEMENT_CHANNEL_NAME;
    private static final Address EVERYBODY = ChatConfig.EVERYBODY;

    private final Map<ChannelName, JChannel> channels = new HashMap<>();
    private JChannel currentChannel;
    private String nickName;
    private final JChannel managementChannel;

    public ChannelsHandler(String nickName) {
        this.nickName = nickName;
        this.managementChannel = connectToManagementChannel();
    }

    private JChannel connectToManagementChannel() {
        JChannel channel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);
        ChatConfig.buildProtocolStack(stack);

        channel.setReceiver(new ManagementChannelReceiver(this));
        try {
            channel.connect(MANAGEMENT_CHANNEL_NAME);
        } catch (Exception e) {
            System.out.println("Cannot connect to management channel");
            e.printStackTrace();
        }

        return channel;
    }

    public void registerNewChannel(ChannelName name, JChannel channel) {
        sendJoinNotification(name);

        if (channels.get(name) != null) {
            System.out.printf("Channel %s already exist! Joining...\n", name);
        } else {
            channels.put(name, channel);
        }
        switchChannel(name);
    }

    private void sendJoinNotification(ChannelName name) {
        ChatOperationProtos.ChatAction build = ChatOperationProtos.ChatAction.newBuilder()
                .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN)
                .setNickname(nickName)
                .setChannel(name.toString())
                .build();
        try {
            managementChannel.send(EVERYBODY, build.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchChannel(ChannelName channelName) {
        JChannel channel = channels.get(channelName);

        if (channel == null) {
            System.out.println("No such channel! Staying on last channel");
            return;
        }

        this.currentChannel = channel;
    }

    public JChannel currentChannel() {
        return this.currentChannel;
    }

    public String getNickName() {
        return nickName;
    }
}
