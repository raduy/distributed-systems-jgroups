package pl.edu.agh.dsrg.sr.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannelReceiver extends ReceiverAdapter {

    private JChannel jChannel;
    private final String nickName;
    private final ChannelName channelName;
    private final ChannelsService channelsService;

    public ChatChannelReceiver(JChannel jChannel, String nickName, ChannelName channelName, ChannelsService channelsService) {
        this.jChannel = jChannel;
        this.nickName = nickName;
        this.channelName = channelName;
        this.channelsService = channelsService;
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

//            System.out.printf(ChatConfig.promptFormat() + "%s", nickname, channelName, connectedUsersCount, message);

            String name = this.jChannel.getName(srcAddress);
            int size = this.jChannel.getView().size();
            System.out.printf("\n" + ChatConfig.promptFormat() + "%s", name, channelName, size, message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
