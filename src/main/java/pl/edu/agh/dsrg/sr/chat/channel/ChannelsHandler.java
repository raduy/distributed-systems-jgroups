package pl.edu.agh.dsrg.sr.chat.channel;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.command.ChatChannelReceiver;
import pl.edu.agh.dsrg.sr.chat.command.ManagementChannelReceiver;
import pl.edu.agh.dsrg.sr.chat.command.MessageContext;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelsHandler implements Iterable<ChatChannel> {
    private static final String MANAGEMENT_CHANNEL_NAME = ChatConfig.MANAGEMENT_CHANNEL_NAME;
    private static final Address EVERYBODY = ChatConfig.EVERYBODY;

    private final Map<ChannelName, ChatChannel> channels = new HashMap<>();
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

        ManagementChannelReceiver managementChannelReceiver = new ManagementChannelReceiver(this);
        channel.setReceiver(managementChannelReceiver);
        try {
            channel.connect(MANAGEMENT_CHANNEL_NAME);
            channel.getState(null, 1000);
        } catch (Exception e) {
            System.out.println("Cannot connect to management channel");
            e.printStackTrace();
        }

        return channel;
    }

    public void registerNewChannel(ChannelName name, JChannel jChannel) {
        ChatChannel channel = channels.get(name);
        if (channel != null) {
            System.out.printf("Channel %s already exist! Joining...\n", name);
            channel.connectMe();
        } else {
            ChatChannel chatChannel = new ChatChannel(name.toString(), jChannel);
            channels.put(name, chatChannel);
        }
        switchChannel(name);

        sendJoinNotification(name);
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
        JChannel channel = channels.get(channelName).getJChannel();

        if (channel == null) {
            System.out.println("No such channel! Staying on last channel");
            return;
        }

        this.currentChannel = channel;
    }

    public JChannel currentChannel() {
        return this.currentChannel;
    }

    public void addUser(User user, String channelRawName) {
        ChannelName channelName = new ChannelName(channelRawName);

        ChatChannel chatChannel = channels.get(channelName);
        if (chatChannel == null) {
            chatChannel = addChannelToState(user.getNickname(), channelRawName);
        }

        chatChannel.connectUser(user);

        System.out.println("User added to state" + user + channelRawName);
    }

    public ChatChannel addChannelToState(String nickName, String channelRawName) {
        JChannel jChannel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        jChannel.setProtocolStack(stack);
        ChannelName channelName = new ChannelName(channelRawName);
        ChatConfig.buildProtocolStack(stack, channelName);

        jChannel.setReceiver(new ChatChannelReceiver(nickName, channelName, this));
        ChatChannel chatChannel = new ChatChannel(channelRawName, jChannel);
        channels.put(channelName, chatChannel);
        return chatChannel;
    }

    public void removeUser(User user, String channel) {
        this.channels.get(new ChannelName(channel)).disconnectUser(user);

        System.out.println("User removed from state" + user + channel);
    }

    public String getNickName() {
        return nickName;
    }

    public MessageContext messageContext(ChannelName channelName, Address srcAddress) {
        ChatChannel chatChannel = this.channels.get(channelName);
        User messageSender = chatChannel.findUser(srcAddress);

        return new MessageContext(messageSender, chatChannel);
    }

    @Override
    public Iterator<ChatChannel> iterator() {
        return this.channels.values().iterator();
    }
}
