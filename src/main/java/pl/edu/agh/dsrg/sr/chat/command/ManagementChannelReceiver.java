package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.channel.User;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ManagementChannelReceiver extends ReceiverAdapter {
    private final ChannelsHandler channelsHandler;

    public ManagementChannelReceiver(ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        ChatOperationProtos.ChatState.Builder stateBuilder = ChatOperationProtos.ChatState.newBuilder();

        for (ChatChannel chatChannel : channelsHandler) {
            for (User user : chatChannel.getUsers()) {
                ChatOperationProtos.ChatAction action = ChatOperationProtos.ChatAction.newBuilder()
                        .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN)
                        .setChannel(chatChannel.getChannelRawName())
                        .setNickname(user.getNickname())
                        .build();

                stateBuilder.addState(action);
            }
        }
        output.write(stateBuilder.build().toByteArray());

        System.out.println("getState");
    }

    @Override
    public void setState(InputStream input) throws Exception {
        ChatOperationProtos.ChatState chatState = ChatOperationProtos.ChatState.parseFrom(input);

        if (chatState == null) {
            return;
        }

        List<ChatOperationProtos.ChatAction> actions = chatState.getStateList();
        for (ChatOperationProtos.ChatAction action : actions) {
            this.channelsHandler.addUser(new User(action.getNickname()), action.getChannel());
        }

        System.out.println("setState");
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("<Logger>: View changed: " + newView);
    }

    @Override
    public void receive(Message msg) {
        ChatOperationProtos.ChatAction chatAction;
        Address srcAddress = msg.getSrc();
        try {
            chatAction = ChatOperationProtos.ChatAction.parseFrom(msg.getBuffer());

            User user = new User(srcAddress, chatAction.getNickname());
            String channelName = chatAction.getChannel();

            switch (chatAction.getAction()) {
                case JOIN:
                    channelsHandler.addUser(user, channelName);
                    System.out.printf("<Management channel> User %s joined %s channel\n", chatAction.getNickname(), channelName);
                    break;
                case LEAVE:
                    channelsHandler.removeUser(user, channelName);
                    System.out.printf("<Management channel> User %s left %s channel\n", chatAction.getNickname(), channelName);
            }

        } catch (InvalidProtocolBufferException e) {
            System.out.println("Protobuf marshalling exception!");
            e.printStackTrace();
        }
    }
}
