package pl.edu.agh.dsrg.sr.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.domain.User;
import pl.edu.agh.dsrg.sr.chat.domain.channel.*;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ManagementChannelReceiver extends ReceiverAdapter {
    private final ChatChannelRepository channelRepository;
    private final ChannelFactory factory;

    public ManagementChannelReceiver(ChatChannelRepository channelRepository,
                                     ChannelFactory factory) {
        this.channelRepository = channelRepository;
        this.factory = factory;
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        ChatOperationProtos.ChatState.Builder stateBuilder
                = ChatOperationProtos.ChatState.newBuilder();

        for (ChatChannel chatChannel : channelRepository) {
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

            ChatChannel chatChannel = channelRepository.loadByNameOrNew(channelName, factory);
            chatChannel.connectUser(new User(action.getNickname()));
        }
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("\r<Logger>: View changed: " + newView);
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

            ChatChannel chatChannel = channelRepository.loadByNameOrNew(channelName, factory);
            switch (chatAction.getAction()) {
                case JOIN:
                    chatChannel.connectUser(user);
                    System.out.printf("\r<Management channel> User %s joined %s channel\n",
                            nickname, channelRawName);
                    break;
                case LEAVE:
                    chatChannel.disconnectUser(user);
                    System.out.printf("\r<Management channel> User %s left %s channel\n",
                            nickname, channelRawName);
            }
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Protobuf marshalling exception!");
            e.printStackTrace();
        }
    }
}
