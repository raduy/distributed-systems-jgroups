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

    public ChannelsService(String nickName,
                           ChatChannelRepository channelRepository,
                           ChannelFactory channelFactory) {
        this.nickName = nickName;
        this.channelRepository = channelRepository;
        this.channelFactory = channelFactory;
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

    public ChatChannel registerNewChannel(ChatChannel chatChannel) {
        ChannelName name = chatChannel.channelName();
        ChatChannel alreadyExistingChannel = channelRepository.loadByName(name);

        if (alreadyExistingChannel != null) {
            System.out.printf("Channel %s already exist! Joining...\n", name);
            switchChannel(name);
            if (!alreadyExistingChannel.amConnected()) {
                sendJoinNotification(name);
            }
            return alreadyExistingChannel;
        }

        channelRepository.add(chatChannel);
        switchChannel(name);
        sendJoinNotification(name);

        return chatChannel;
    }

    private void sendJoinNotification(ChannelName channelName) {
        sendNotification(channelName, ChatOperationProtos.ChatAction.ActionType.JOIN);
    }

    private void sendLeaveNotification(ChannelName channelName) {
        sendNotification(channelName, ChatOperationProtos.ChatAction.ActionType.LEAVE);
    }

    private void sendNotification(ChannelName channelName, ChatOperationProtos.ChatAction.ActionType actionType) {
        ChatOperationProtos.ChatAction build = ChatOperationProtos.ChatAction.newBuilder()
                .setAction(actionType)
                .setNickname(nickName)
                .setChannel(channelName.toString())
                .build();
        try {
            managementChannel.send(EVERYBODY, build.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChatChannel currentChannel() {
        return this.currentChannel;
    }

    public String getNickName() {
        return nickName;
    }

    public void switchChannel(ChannelName channelName) {
        this.currentChannel = channelRepository.loadByName(channelName);
    }

    public void closeCurrentChannel() {
        if (currentChannel() == null) {
            System.out.println("No channel to leave!");
            return;
        }

        ChatChannel chatChannel = currentChannel();
        chatChannel.getJChannel().close();

        sendLeaveNotification(chatChannel.channelName());

        currentChannel = channelRepository.iterator().next();

        System.out.printf("\rChannel %s closed.\n", chatChannel.rawName());
    }
}
