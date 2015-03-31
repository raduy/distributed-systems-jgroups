package pl.edu.agh.dsrg.sr.chat.command;

import org.jgroups.JChannel;
import pl.edu.agh.dsrg.sr.chat.domain.MalformedMulticastAddressException;
import pl.edu.agh.dsrg.sr.chat.domain.channel.*;

import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
class CreateNewChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-n";
    public static final String USAGE = "-n <channelName>";
    public static final String DESCRIPTION = "Creates new channel (channelName must be a multicast address)";

    private final ChannelName channelName;
    private final ChannelsService channelsService;
    private final ChannelFactory channelFactory;

    CreateNewChannelCommand(String command,
                            ChannelsService channelsService,
                            ChannelFactory channelFactory) {
        this.channelsService = channelsService;
        this.channelFactory = channelFactory;
        this.channelName = parseChannelName(command);
    }

    private ChannelName parseChannelName(String command) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String[] split = command.split("\\s+");
                String rawChannelName = split[1];
                return new ChannelName(rawChannelName);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Missing channel name! Try again");
            } catch (MalformedMulticastAddressException e) {
                System.out.println("Channel address must be a correct multicast IPv4 address!");
            }

            command = scanner.nextLine();
        }
    }

    @Override
    public void execute() {
        try {
            ChatChannel chatChannel = channelFactory.create(channelName);

            ChatChannel newChannel = channelsService.registerNewChannel(chatChannel);
            JChannel jChannel = newChannel.getJChannel();

            jChannel.setName(channelsService.getNickName());
            jChannel.connect(channelName.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
