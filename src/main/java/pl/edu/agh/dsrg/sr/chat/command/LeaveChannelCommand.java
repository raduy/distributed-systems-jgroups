package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class LeaveChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-l";
    public static final String USAGE = "-l";
    public static final String DESCRIPTION = "Leaves current channel by closing it.";
    private final ChannelsService channelsService;

    public LeaveChannelCommand(ChannelsService channelsService) {
        this.channelsService = channelsService;
    }

    @Override
    public void execute() {
        channelsService.closeCurrentChannel();
    }
}
