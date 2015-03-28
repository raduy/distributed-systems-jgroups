package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.channel.MalformedMulticastAddressException;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class SwitchToChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-s";
    public static final String USAGE = "-s <channelName>";
    public static final String DESCRIPTION = "Switches to existing channel (channelName must be a multicast address)";
    private final String command;
    private final ChannelsHandler channelsHandler;

    public SwitchToChannelCommand(String command, ChannelsHandler channelsHandler) {
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
