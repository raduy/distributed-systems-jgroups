package pl.agh.edu.sr.raduy.command;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class JGroupsReceiver extends ReceiverAdapter {

    public void viewAccepted(View new_view) {
//        view[0] = new_view;
        System.out.println("View changed! ");
        System.out.println("view: " + new_view);
    }

    public void receive(Message msg) {
        Address sender = msg.getSrc();
        System.out.println(msg.getObject() + " [" + sender + "]");
    }
}
