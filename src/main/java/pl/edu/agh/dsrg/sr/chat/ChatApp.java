package pl.edu.agh.dsrg.sr.chat;

import org.jgroups.Address;
import org.jgroups.JChannel;
import pl.edu.agh.dsrg.sr.chat.channel.ChannelsHandler;
import pl.edu.agh.dsrg.sr.chat.command.CommandRouter;
import pl.edu.agh.dsrg.sr.chat.command.ICommand;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.util.Scanner;

import static pl.edu.agh.dsrg.sr.chat.config.ChatConfig.promptFormat;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatApp {
    private final static Address EVERYBODY = ChatConfig.EVERYBODY;

    private static ChannelsHandler channelsHandler;
    private static CommandRouter router;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printChatIntro();

        String nickName = readNickName(scanner);
        System.out.printf("Hello %s! \nLoading...\n", nickName);

        channelsHandler = new ChannelsHandler(nickName);

        System.out.printf("Done! Here are your options:\n");
        router = new CommandRouter(channelsHandler);

        CommandRouter.printAvailableCommands();
        chatMode();
    }

    private static String readNickName(Scanner scanner) {
        System.out.println("What's your name?");
        String nickName;

        while(true) {
            nickName = scanner.nextLine();
            if (nickName.isEmpty()) {
                System.out.println("You really don't have a name?");
                continue;
            }
            return nickName;
        }
    }

    public static void chatMode() {
        while (!Thread.interrupted()) {
            JChannel channel = channelsHandler.currentChannel();
            String nickName = channelsHandler.getNickName();
            String channelName;

            if (channel != null) {
                channelName = channel.getClusterName();
                int channelSize = channel.getView().getMembers().size();

                System.out.printf(promptFormat(), nickName, channelName, channelSize);
            } else {
                System.out.printf(promptFormat(), nickName, "no channel! ", 0);
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
                    ChatOperationProtos.ChatMessage message = ChatOperationProtos.ChatMessage.newBuilder()
                            .setMessage(userInput)
                            .build();

                    channel.send(EVERYBODY, message.toByteArray());
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
        System.out.println("\t\t\t##################################");
        System.out.println("\t\t\t#  Welcome to raduy's chat app!  #");
        System.out.println("\t\t\t##################################");
    }
}
