package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ManagementChannelReceiver extends ReceiverAdapter {
    private final ChannelsHandler channelsHandler;

    public ManagementChannelReceiver(ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
    }

    public void viewAccepted(View new_view) {
//        view[0] = new_view;
        System.out.printf("\nView changed! \n");
        System.out.println("view: " + new_view);
    }

    public void receive(Message msg) {
        ChatOperationProtos.ChatAction chatAction;
        try {
            chatAction = ChatOperationProtos.ChatAction.parseFrom(msg.getBuffer());

            switch(chatAction.getAction()) {
                case LEAVE:
                    System.out.printf("Management: User %s left %s channel\n", chatAction.getNickname(), chatAction.getChannel());
                    break;
               case JOIN:
                   System.out.printf("Management: User %s joined %s channel\n", chatAction.getNickname(), chatAction.getChannel());
            }

        } catch (InvalidProtocolBufferException e) {
            System.out.println("Protobuf marshalling exception!");
            e.printStackTrace();
        }
    }
}
