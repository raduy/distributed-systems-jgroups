package pl.edu.agh.dsrg.sr.chat.command;

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

    public CommandRouter(ChannelsService channelsService, ChatChannelRepository channelRepository) {
        this.channelsService = channelsService;
        this.channelRepository = channelRepository;
    }

    static {
        commands.put(CreateNewChannelCommand.USAGE, CreateNewChannelCommand.DESCRIPTION);
        commands.put(SwitchToChannelCommand.USAGE, SwitchToChannelCommand.DESCRIPTION);
        commands.put(ShowAllChannelsCommand.USAGE, ShowAllChannelsCommand.DESCRIPTION);
    }

    public static void printAvailableCommands() {
        for (String s : commands.keySet()) {
            System.out.println(s + " " + commands.get(s));
        }
    }

    public ICommand matchCommand(String cmd) {
        if (cmd.startsWith(CreateNewChannelCommand.INVOCATION_PREFIX)) {
            return new CreateNewChannelCommand(cmd, channelsService, channelRepository);
        }

        if (cmd.startsWith(SwitchToChannelCommand.INVOCATION_PREFIX)) {
            return new SwitchToChannelCommand(cmd, channelsService);
        }

        if (cmd.startsWith(ShowAllChannelsCommand.INVOCATION_PREFIX)) {
            return new ShowAllChannelsCommand(channelRepository);
        }

        return new PrintHelpCommand();
    }
}
