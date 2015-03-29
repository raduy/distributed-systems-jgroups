package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.channel.User;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ManagementChannelReceiver extends ReceiverAdapter {
    private final ChannelsHandler channelsHandler;

    public ManagementChannelReceiver(ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
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
