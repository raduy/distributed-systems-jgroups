package pl.agh.edu.sr.raduy.command;

import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.agh.edu.sr.raduy.channel.ChannelName;
import pl.agh.edu.sr.raduy.channel.ChannelsHandler;
import pl.agh.edu.sr.raduy.channel.MalformedMulticastAddressException;
import pl.agh.edu.sr.raduy.config.ChatConfig;

import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class CreateNewChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-n";
    public static final String USAGE = "-n <channelName>";
    public static final String DESCRIPTION = "Creates new channel (channelName must be a multicast address)";

    private ChannelName channelName;
    private ChannelsHandler channelsHandler;

    public CreateNewChannelCommand(String command, ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
        this.channelName = parseChannelName(command);
    }

    private ChannelName parseChannelName(String command) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String[] split = command.split("\\s+");
                String rawChannelName = split[1];
                return channelName = new ChannelName(rawChannelName);

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
            JChannel channel = new JChannel(false);

            ProtocolStack stack = new ProtocolStack();
            channel.setProtocolStack(stack);
            ChatConfig.buildProtocolStack(stack);

            channel.setReceiver(new JGroupsReceiver());
            channel.connect(channelName.toString());

            channelsHandler.registerNewChannel(channelName, channel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}