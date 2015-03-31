package pl.edu.agh.dsrg.sr.chat.command;

import org.jgroups.JChannel;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.domain.MalformedMulticastAddressException;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.receiver.ChatChannelReceiver;

import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
class CreateNewChannelCommand implements ICommand {
    public static final String INVOCATION_PREFIX = "-n";
    public static final String USAGE = "-n <channelName>";
    public static final String DESCRIPTION = "Creates new channel (channelName must be a multicast address)";

    private ChannelName channelName;
    private final ChannelsService channelsService;

    CreateNewChannelCommand(String command, ChannelsService channelsService) {
        this.channelsService = channelsService;
        this.channelName = parseChannelName(command);
    }

    private ChannelName parseChannelName(String command) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String[] split = command.split("\\s+");
                String rawChannelName = split[1];
                return channelName = new ChannelName(rawChannelName);

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
            JChannel jChannel = new JChannel(false);

            ProtocolStack stack = new ProtocolStack();
            jChannel.setProtocolStack(stack);
            ChatConfig.buildProtocolStack(stack, channelName);

            jChannel.setReceiver(new ChatChannelReceiver(jChannel, channelsService.getNickName(), channelName));
            channelsService.registerNewChannel(channelName, jChannel);

            jChannel.setName(this.channelsService.getNickName());
            jChannel.connect(channelName.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
