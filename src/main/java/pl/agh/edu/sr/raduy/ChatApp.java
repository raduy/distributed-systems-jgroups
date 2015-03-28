package pl.agh.edu.sr.raduy;

import org.jgroups.Address;
import org.jgroups.JChannel;
import pl.agh.edu.sr.raduy.channel.ChannelsHandler;
import pl.agh.edu.sr.raduy.command.CommandRouter;
import pl.agh.edu.sr.raduy.command.ICommand;

import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatApp {
    private final static Address EVERYBODY = null; /* null means everybody for JGroups ~_~ */

    private static ChannelsHandler channelsHandler;
    private static CommandRouter router;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printChatIntro();

        String nickName = readNickName(scanner);
        channelsHandler = new ChannelsHandler(nickName);
        router = new CommandRouter(channelsHandler);

        CommandRouter.printAvailableCommands();
        chatMode();
    }

    private static String readNickName(Scanner scanner) {
        System.out.println("What's your name?");
        String nickName = scanner.nextLine();
        System.out.printf("Hello %s! \nHere are your options:\n", nickName);
        return nickName;
    }

    public static void chatMode() {
        while (!Thread.interrupted()) {
            JChannel channel = channelsHandler.currentChannel();
            String channelName;
            String nickName;

            if (channel != null) {
                channelName = channel.getClusterName();
                nickName = channelsHandler.getNickName();
                int channelSize = channel.getView().getMembers().size();

                System.out.printf("<%s @%s(online: %d users)>", nickName, channelName, channelSize);
            } else {
                System.out.println("No channel exist! Join some or create new one. Hit --help for more info");
            }

            String userInput = scanner.nextLine();
            if (userInput.startsWith("-")) {
                commandMode(userInput);
                continue;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            if (channel != null) {
                try {
                    channel.send(EVERYBODY, userInput);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void commandMode(String userInput) {
        ICommand command = router.matchCommand(userInput);
        command.execute();
    }

    private static void printChatIntro() {
        System.out.println("\t\t\t######################################");
        System.out.println("\t\t\t#  Welcome to raduy's chatMode app!  #");
        System.out.println("\t\t\t######################################");
    }
}
