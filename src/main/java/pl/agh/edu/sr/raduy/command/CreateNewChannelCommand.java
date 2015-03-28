package pl.agh.edu.sr.raduy.command;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;
import pl.agh.edu.sr.raduy.channel.ChannelName;
import pl.agh.edu.sr.raduy.channel.ChannelsHandler;
import pl.agh.edu.sr.raduy.channel.MalformedMulticastAddressException;

import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class CreateNewChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-n";
    public static final String DESCRIPTION = "Create new channel: -n <channelName (multicast address)>";

    private ChannelName channelName;
    private ChannelsHandler channelsHandler;

    public CreateNewChannelCommand(String command, ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String[] split = command.split("\\s+");
                String rawChannelName = split[1];
                channelName = new ChannelName(rawChannelName);
                break;

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Missing channel name! Try again");
            } catch (MalformedMulticastAddressException e) {
                System.out.println("Channel address must be a correct multicast IPv4 address!");
            }

            command = scanner.nextLine();
        }
    }

    @Override
    public void execute() {
        try {
            JChannel ch = new JChannel(false);
            channelsHandler.registerNewChannel(channelName, ch);
            ProtocolStack stack = new ProtocolStack();
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

            final View[] view = new View[1];
            ch.setReceiver(new ReceiverAdapter() {
                public void viewAccepted(View new_view) {
                    view[0] = new_view;
                    System.out.println("view: " + new_view);
                }

                public void receive(Message msg) {
                    Address sender = msg.getSrc();
                    System.out.println(msg.getObject() + " [" + sender + "]");
                }
            });

            ch.connect(channelName.toString());


            System.out.println("as" + ch.getAddress());

            for (; ; ) {
                System.out.println(view[0]);
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                ch.send(null, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
