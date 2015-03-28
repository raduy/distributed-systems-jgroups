package pl.agh.edu.sr.raduy;

import pl.agh.edu.sr.raduy.channel.ChannelsHandler;
import pl.agh.edu.sr.raduy.command.CommandRouter;
import pl.agh.edu.sr.raduy.command.ICommand;

import java.util.Scanner;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatApp {
    public static void main(String[] args) {
        printChatIntro();

        Scanner scanner = new Scanner(System.in);

        String nickName = readNickName(scanner);
        ChannelsHandler channelsHandler = new ChannelsHandler(nickName);
        CommandRouter dispatcher = new CommandRouter(channelsHandler);

        CommandRouter.printAvailableCommands();
        startInteractiveMode(scanner, dispatcher);
    }

    private static String readNickName(Scanner scanner) {
        System.out.println("What's your name?");
        String nickName = scanner.nextLine();
        System.out.printf("Hello %s! Here are your options:\n", nickName);
        return nickName;
    }

    private static void startInteractiveMode(Scanner scanner, CommandRouter dispatcher) {
        System.out.print("> ");
        String userInput = scanner.nextLine();
        ICommand command = dispatcher.matchCommand(userInput);
        command.execute();
    }

    private static void printChatIntro() {
        System.out.println("\t\t\tWelcome to raduy's chat app!");
    }

}
