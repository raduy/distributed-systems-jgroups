package pl.edu.agh.dsrg.sr.chat.command;

import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannelRepository;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
class ShowAllChannelsCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-c";
    public static final String USAGE = "-c";
    public static final String DESCRIPTION = "Shows all (existing channels)";
    private final ChatChannelRepository channelRepository;

    ShowAllChannelsCommand(ChatChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void execute() {
        if (channelRepository.immutableView().isEmpty()) {
            System.out.println("No channels exist! Create new with -n command. --help for more");
            return;
        }

        for (ChatChannel chatChannel : channelRepository) {
            System.out.println(chatChannel);
        }
    }
}
