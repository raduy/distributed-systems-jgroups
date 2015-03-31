package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.domain.MalformedMulticastAddressException;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
class SwitchToChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-s";
    public static final String USAGE = "-s <channelName>";
    public static final String DESCRIPTION = "Switches to existing channel (channelName must be a multicast address)";
    private final String command;
    private final ChannelsService channelsService;

    SwitchToChannelCommand(String command, ChannelsService channelsService) {
        this.command = command;
        this.channelsService = channelsService;
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
            this.channelsService.switchChannel(channelName);
        } catch (MalformedMulticastAddressException e) {
            System.out.println("Malformed channel name! Must be a correct multicast address");
        }
    }
}
