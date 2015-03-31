package pl.edu.agh.dsrg.sr.chat.domain.channel;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import pl.edu.agh.dsrg.sr.chat.receiver.ManagementChannelReceiver;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelsService {
    private static final String MANAGEMENT_CHANNEL_NAME = ChatConfig.MANAGEMENT_CHANNEL_NAME;
    private static final Address EVERYBODY = ChatConfig.EVERYBODY;

    private final ChatChannelRepository channelRepository;

    private ChatChannel currentChannel;
    private String nickName;
    private JChannel managementChannel;
    private final ChannelFactory channelFactory;

    public ChannelsService(String nickName, ChatChannelRepository channelRepository) {
        this.nickName = nickName;
        this.channelRepository = channelRepository;
        this.channelFactory = new ChannelFactory(nickName);
    }

    public JChannel connectToManagementChannel() {
        JChannel channel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);
        ChatConfig.buildProtocolStack(stack);

        ManagementChannelReceiver managementChannelReceiver
                = new ManagementChannelReceiver(channelRepository, channelFactory);
        channel.setReceiver(managementChannelReceiver);
        try {
            channel.setName(this.nickName);
            channel.connect(MANAGEMENT_CHANNEL_NAME);
            channel.getState(null, 1000);
        } catch (Exception e) {
            System.out.println("Cannot connect to management channel");
            e.printStackTrace();
        }

        this.managementChannel = channel;
        return channel;
    }

    public void registerNewChannel(ChannelName name, JChannel jChannel) {
        ChatChannel channel = channelRepository.loadByName(name);

        if (channel != null) {
            System.out.printf("Channel %s already exist! Joining...\n", name);
//            channel.connectMe();
        } else {
            ChatChannel chatChannel = new ChatChannel(name, jChannel);
            channelRepository.add(chatChannel);
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
        ChatChannel chatChannel = channelRepository.loadByName(channelName);

        if (chatChannel == null) {
            System.out.println("No such channel! Staying on last channel");
            return;
        }

        this.currentChannel = chatChannel;
    }

    public ChatChannel currentChannel() {
        return this.currentChannel;
    }

    public String getNickName() {
        return nickName;
    }
}
