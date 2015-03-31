package pl.edu.agh.dsrg.sr.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannelReceiver extends ReceiverAdapter {

    private final JChannel jChannel;
    private final String nickName;
    private final ChannelName channelName;

    public ChatChannelReceiver(JChannel jChannel, String nickName, ChannelName channelName) {
        this.jChannel = jChannel;
        this.nickName = nickName;
        this.channelName = channelName;
    }

    @Override
    public void viewAccepted(View newView) {
        System.out.println("<Logger>: View changed: " + newView);
    }

    @Override
    public void receive(Message msg) {
        if (isMyMessage(msg)) {
            return;
        }

        String message;
        Address srcAddress = msg.getSrc();
        try {
            message = ChatOperationProtos.ChatMessage.parseFrom(msg.getBuffer()).getMessage();

            String nickName = jChannel.getName(srcAddress);
            int membersInChannelCount = jChannel.getView().size();

            System.out.printf("\n" + ChatConfig.promptFormat() + "%s", nickName, channelName, membersInChannelCount, message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyMessage(Message msg) {
        return jChannel.getName(msg.getSrc()).equals(nickName);
    }
}
