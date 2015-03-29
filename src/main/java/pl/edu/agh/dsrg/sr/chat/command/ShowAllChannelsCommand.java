package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.channel.ChatChannel;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ShowAllChannelsCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-c";
    public static final String USAGE = "-c";
    public static final String DESCRIPTION = "Shows all (existing channels)";
    private final ChannelsHandler channelsHandler;

    public ShowAllChannelsCommand(String cmd, ChannelsHandler channelsHandler) {
        this.channelsHandler = channelsHandler;
    }

    @Override
    public void execute() {
        if (!channelsHandler.iterator().hasNext()) {
            System.out.println("No channels exist! Create new with -n command. --help for more");
            return;
        }

        for (ChatChannel chatChannel : channelsHandler) {
            System.out.println(chatChannel);
        }
    }
}
