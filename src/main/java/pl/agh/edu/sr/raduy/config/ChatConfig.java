package pl.agh.edu.sr.raduy.config;

import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatConfig {

    public static ProtocolStack buildProtocolStack(ProtocolStack stack) {
        try {
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
                    .addProtocol(new FRAG2());
            stack.init();
        } catch (Exception e) {
            System.err.printf("Error creating protocol stack! Cause: %s", e);
        }

        return stack;
    }
}
