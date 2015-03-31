package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.domain.User;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannelRepository;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ManagementChannelReceiver extends ReceiverAdapter {
    private final ChatChannelRepository channelRepository;
    private final ChannelsService channelsService;

    public ManagementChannelReceiver(ChatChannelRepository channelRepository,
                                     ChannelsService channelsService) {
        this.channelRepository = channelRepository;
        this.channelsService = channelsService;
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        ChatOperationProtos.ChatState.Builder stateBuilder
                = ChatOperationProtos.ChatState.newBuilder();

        for (ChatChannel chatChannel : channelRepository.immutableView()) {
            appendUsers(stateBuilder, chatChannel);
        }
        output.write(stateBuilder.build().toByteArray());
    }

    private void appendUsers(ChatOperationProtos.ChatState.Builder stateBuilder,
                             ChatChannel chatChannel) {

        for (User user : chatChannel.getUsers()) {
            ChatOperationProtos.ChatAction action = ChatOperationProtos.ChatAction
                    .newBuilder()
                    .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN)
                    .setChannel(chatChannel.rawName())
                    .setNickname(user.getNickname())
                    .build();

            stateBuilder.addState(action);
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        ChatOperationProtos.ChatState chatState
                = ChatOperationProtos.ChatState.parseFrom(input);

        if (chatState == null) {
            return;
        }

        List<ChatOperationProtos.ChatAction> actions = chatState.getStateList();
        for (ChatOperationProtos.ChatAction action : actions) {
            ChannelName channelName = new ChannelName(action.getChannel());

            ChatChannel chatChannel = channelRepository.loadByName(channelName);
            if (chatChannel == null) {
                chatChannel = channelsService.createNewChannel(channelName);
                channelRepository.add(chatChannel);
            }

            chatChannel.connectUser(new User(action.getNickname()));
        }
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("<Logger>: View changed: " + newView);
    }

    @Override
    public void receive(Message msg) {
        try {
            ChatOperationProtos.ChatAction chatAction
                    = ChatOperationProtos.ChatAction.parseFrom(msg.getBuffer());

            String nickname = chatAction.getNickname();
            User user = new User(nickname);

            String channelRawName = chatAction.getChannel();
            ChannelName channelName = new ChannelName(channelRawName);

            ChatChannel chatChannel = channelRepository.loadByName(channelName);
            if (chatChannel == null) {
                chatChannel = channelsService.createNewChannel(channelName);
                channelRepository.add(chatChannel);
            }

            switch (chatAction.getAction()) {
                case JOIN:
                    chatChannel.connectUser(user);
                    System.out.printf("<Management channel> User %s joined %s channel\n",
                            nickname, channelRawName);
                    break;
                case LEAVE:
                    chatChannel.disconnectUser(user);
                    System.out.printf("<Management channel> User %s left %s channel\n",
                            nickname, channelRawName);
            }

        } catch (InvalidProtocolBufferException e) {
            System.out.println("Protobuf marshalling exception!");
            e.printStackTrace();
        }
    }
}
