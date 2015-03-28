package pl.agh.edu.sr.raduy.command;

import pl.agh.edu.sr.raduy.channel.ChannelsHandler;

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
        commands.put(CreateNewChannelCommand.INVOCATION_PREFIX, CreateNewChannelCommand.DESCRIPTION);
    }

    public static void printAvailableCommands() {
        for (String s : commands.keySet()) {
            System.out.println(s + " " + commands.get(s));
        }
    }

    public ICommand matchCommand(String command) {
        if (command.startsWith(CreateNewChannelCommand.INVOCATION_PREFIX)) {
            return new CreateNewChannelCommand(command, channelsHandler);
        }

        return new PrintHelpCommand();
    }
}
