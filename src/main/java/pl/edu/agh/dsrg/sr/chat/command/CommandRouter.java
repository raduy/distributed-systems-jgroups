package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class CommandRouter {
    private static final Map<String, String> commands = new HashMap<>();
    private final ChannelsHandler channelsHandler;

    public CommandRouter(ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
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
            return new CreateNewChannelCommand(cmd, channelsHandler);
        }

        if (cmd.startsWith(SwitchToChannelCommand.INVOCATION_PREFIX)) {
            return new SwitchToChannelCommand(cmd, channelsHandler);
        }

        if (cmd.startsWith(ShowAllChannelsCommand.INVOCATION_PREFIX)) {
            return new ShowAllChannelsCommand(cmd, channelsHandler);
        }

        return new PrintHelpCommand();
    }
}
