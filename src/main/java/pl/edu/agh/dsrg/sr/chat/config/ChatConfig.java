package pl.edu.agh.dsrg.sr.chat.config;

import org.jgroups.Address;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;

import java.net.InetAddress;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatConfig {
    public static final String MANAGEMENT_CHANNEL_NAME = "ChatManagement768624";
    public static final Address EVERYBODY = null; /* null means everybody for JGroups ~_~ */

    //TODO remove duplicated code
    public static ProtocolStack buildProtocolStack(ProtocolStack stack, ChannelName channelName) {
        try {
            stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(channelName.toString())))
                    .addProtocol(new PING())
                    .addProtocol(new MERGE2())
                    .addProtocol(new FD_SOCK())
                    .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(new BARRIER())
                    .addProtocol(new NAKACK())
                    .addProtocol(new UNICAST2())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new UFC())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2())
                    .addProtocol(new STATE_TRANSFER())
                    .addProtocol(new FLUSH());
            stack.init();
        } catch (Exception e) {
            System.err.printf("Error creating protocol stack! Cause: %s", e);
        }

        return stack;
    }

    public static ProtocolStack buildProtocolStack(ProtocolStack stack) {
        try {
            stack.addProtocol(new UDP())
                    .addProtocol(new PING())
                    .addProtocol(new MERGE2())
                    .addProtocol(new FD_SOCK())
                    .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                    .addProtocol(new VERIFY_SUSPECT())
                    .addProtocol(new BARRIER())
                    .addProtocol(new NAKACK())
                    .addProtocol(new UNICAST2())
                    .addProtocol(new STABLE())
                    .addProtocol(new GMS())
                    .addProtocol(new UFC())
                    .addProtocol(new MFC())
                    .addProtocol(new FRAG2())
                    .addProtocol(new STATE_TRANSFER())
                    .addProtocol(new FLUSH());
            stack.init();
        } catch (Exception e) {
            System.err.printf("Error creating protocol stack! Cause: %s", e);
        }

        return stack;
    }

    public static String promptFormat() {
        //<%nickName @%channelName(online: %usersInThisChannelCount users)>
        return "<%s @%s(online: %d users)>";
    }
}
