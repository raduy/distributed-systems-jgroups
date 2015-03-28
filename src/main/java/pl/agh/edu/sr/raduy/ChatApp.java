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
        commandMode();
    }

    private static String readNickName(Scanner scanner) {
        System.out.println("What's your name?");
        String nickName = scanner.nextLine();
        System.out.printf("Hello %s! Here are your options:\n", nickName);
        return nickName;
    }

    private static void commandMode() {
        System.out.print("> ");
        String userInput = scanner.nextLine();
        ICommand command = router.matchCommand(userInput);
        command.execute();
    }

    private static void printChatIntro() {
        System.out.println("\t\t\t####################################");
        System.out.println("\t\t\t#   Welcome to raduy's chat app!   #");
        System.out.println("\t\t\t####################################");
    }

    public static void chatMode() {
        JChannel channel = channelsHandler.currentChannel();

        while (!Thread.interrupted()) {
            String line = scanner.nextLine();
            if (line.startsWith("/c")) {
                break; //switch to commandMode
            }
            try {
                channel.send(EVERYBODY, line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        commandMode();
    }
}
