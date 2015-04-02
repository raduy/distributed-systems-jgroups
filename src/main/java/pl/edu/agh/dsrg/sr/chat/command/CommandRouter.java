package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelFactory;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannelRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class CommandRouter {
    private static final Map<String, String> commands = new HashMap<>();
    private final ChannelsService channelsService;
    private final ChatChannelRepository channelRepository;
    private final ChannelFactory channelFactory;

    public CommandRouter(ChannelsService channelsService,
                         ChatChannelRepository channelRepository,
                         ChannelFactory channelFactory) {
        this.channelsService = channelsService;
        this.channelRepository = channelRepository;
        this.channelFactory = channelFactory;
    }

    static {
        commands.put(CreateNewChannelCommand.USAGE, CreateNewChannelCommand.DESCRIPTION);
        commands.put(SwitchToChannelCommand.USAGE, SwitchToChannelCommand.DESCRIPTION);
        commands.put(ShowAllChannelsCommand.USAGE, ShowAllChannelsCommand.DESCRIPTION);
        commands.put(LeaveChannelCommand.USAGE, LeaveChannelCommand.DESCRIPTION);
    }

    public static void printAvailableCommands() {
        for (String s : commands.keySet()) {
            System.out.println(s + " " + commands.get(s));
        }
    }

    public ICommand matchCommand(String cmd) {
        if (cmd.startsWith(CreateNewChannelCommand.INVOCATION_PREFIX)) {
            return new CreateNewChannelCommand(cmd, channelsService, channelFactory);
        }

        if (cmd.startsWith(SwitchToChannelCommand.INVOCATION_PREFIX)) {
            return new SwitchToChannelCommand(cmd, channelsService);
        }

        if (cmd.startsWith(ShowAllChannelsCommand.INVOCATION_PREFIX)) {
            return new ShowAllChannelsCommand(channelRepository);
        }

        if (cmd.startsWith(LeaveChannelCommand.INVOCATION_PREFIX)) {
            return new LeaveChannelCommand(channelsService);
        }

        return new PrintHelpCommand();
    }
}
