package pl.edu.agh.dsrg.sr.chat.learning;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.net.InetAddress;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class SimpleChat {
    public static void main(String[] args) throws Exception {
        JChannel ch = new JChannel(false);         // (1)
        ProtocolStack stack = new ProtocolStack(); // (2)
        ch.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("bind_addr",
                InetAddress.getByName("192.168.0.5")))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000)
                        .setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK())
                .addProtocol(new UNICAST2())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2());       // (3)
        stack.init();                            // (4)

//        new Message(byteArray)

        ch.setReceiver(new ReceiverAdapter() {
            public void viewAccepted(View new_view) {
                System.out.println("view: " + new_view);
            }

            public void receive(Message msg) {
                Address sender = msg.getSrc();
                System.out.println(msg.getObject() + " [" + sender + "]");
            }
        });

        ch.connect("ChatCluster");


        System.out.println("as" + ch.getAddress());

        for (; ; ) {
            String line = Util.readStringFromStdin(": ");
            ch.send(null, line);
        }
    }

}
