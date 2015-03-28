package pl.edu.agh.dsrg.sr.chat.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class JGroupsReceiver extends ReceiverAdapter {

    public void viewAccepted(View new_view) {
//        view[0] = new_view;
        System.out.printf("\nView changed! \n");
        System.out.println("view: " + new_view);
    }

    public void receive(Message msg) {
        String message;
        try {
            message = ChatOperationProtos.ChatMessage.parseFrom(msg.getBuffer()).getMessage();
            System.out.println(message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
