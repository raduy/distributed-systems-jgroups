package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannelReceiver extends ReceiverAdapter {

    private final ChannelName channelName;
    private final ChannelsHandler channelsHandler;

    public ChatChannelReceiver(ChannelName channelName, ChannelsHandler channelsHandler) {
        this.channelName = channelName;
        this.channelsHandler = channelsHandler;
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("<Logger>: View changed: " + newView);
    }

    @Override
    public void receive(Message msg) {
        String message;
        Address srcAddress = msg.getSrc();
        try {
            message = ChatOperationProtos.ChatMessage.parseFrom(msg.getBuffer()).getMessage();

//            MessageContext context = channelsHandler.messageContext(channelName, srcAddress);

//            String nickname = context.getUser().getNickname();
//            int connectedUsersCount = context.getChatChannel()
//                    .getJChannel()
//                    .getView()
//                    .size();

//            System.out.printf(ChatConfig.promptFormat() + "%s", nickname, channelName, connectedUsersCount, message);
            System.out.println(message + srcAddress);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
