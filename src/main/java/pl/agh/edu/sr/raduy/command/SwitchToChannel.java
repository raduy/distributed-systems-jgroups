package pl.agh.edu.sr.raduy.command;

import pl.agh.edu.sr.raduy.channel.ChannelName;
import pl.agh.edu.sr.raduy.channel.ChannelsHandler;
import pl.agh.edu.sr.raduy.channel.MalformedMulticastAddressException;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class SwitchToChannel implements ICommand {
    public static final String INVOCATION_PREFIX = "-n";
    public static final String USAGE = "-n <channelName>";
    public static final String DESCRIPTION = "Creates new channel (channelName must be a multicast address)";
    private final String command;
    private final ChannelsHandler channelsHandler;

    public SwitchToChannel(String command, ChannelsHandler channelsHandler) {
        this.command = command;
        this.channelsHandler = channelsHandler;
    }

    @Override
    public void execute() {
        String[] split = command.split("\\s+");
        if (split.length < 2) {
            System.out.println("No channel id! Try again");
            System.out.println(USAGE);
            return;
        }

        String rawChannelName = split[1];

        ChannelName channelName;
        try {
            channelName = new ChannelName(rawChannelName);
            this.channelsHandler.switchChannel(channelName);
        } catch (MalformedMulticastAddressException e) {
            System.out.println("Malformed channel name! Must be a correct multicast address");
        }
    }
}
