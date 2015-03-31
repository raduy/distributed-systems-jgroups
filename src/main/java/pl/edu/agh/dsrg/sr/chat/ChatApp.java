package pl.edu.agh.dsrg.sr.chat;

import org.jgroups.Address;
import org.jgroups.View;
import pl.edu.agh.dsrg.sr.chat.command.CommandRouter;
import pl.edu.agh.dsrg.sr.chat.command.ICommand;
import pl.edu.agh.dsrg.sr.chat.config.ChatConfig;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelFactory;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelsService;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannelRepository;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.util.Scanner;

import static pl.edu.agh.dsrg.sr.chat.config.ChatConfig.promptFormat;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatApp {
    private final static Address EVERYBODY = ChatConfig.EVERYBODY;

    private final static Scanner scanner = new Scanner(System.in);
    private static ChannelsService channelsService;
    private CommandRouter router;
    private static final String nickName;

    static {
        printChatIntro();
        nickName = readNickName(scanner);
    }

    public static void main(String[] args) {
        new ChatApp().play();
    }

    private void play() {
        System.out.printf("Hello %s! \nLoading...\n", nickName);

        initApp();
        CommandRouter.printAvailableCommands();
        chatMode();
    }

    private void initApp() {
        ChatChannelRepository channelRepository = new ChatChannelRepository();
        ChannelFactory channelFactory = new ChannelFactory(nickName, channelRepository);
        channelsService = new ChannelsService(nickName, channelRepository, channelFactory);

        channelsService.connectToManagementChannel();
        System.out.printf("Done! Here are your options:\n");
        router = new CommandRouter(channelsService, channelRepository, channelFactory);
    }

    private static String readNickName(Scanner scanner) {
        System.out.println("What's your name?");
        String nickName;

        while (true) {
            nickName = scanner.nextLine();
            if (nickName.isEmpty()) {
                System.out.println("You really don't have a name?");
                continue;
            }
            return nickName;
        }
    }

    public void chatMode() {
        while (!Thread.interrupted()) {
            ChatChannel chatChannel = channelsService.currentChannel();

            printContext();

            String userInput = scanner.nextLine();
            if (userInput.startsWith("-")) {
                commandMode(userInput);
                continue;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            if (chatChannel != null && chatChannel.getJChannel() != null) {
                try {
                    ChatOperationProtos.ChatMessage message = ChatOperationProtos.ChatMessage.newBuilder()
                            .setMessage(userInput)
                            .build();

                    chatChannel.getJChannel().send(EVERYBODY, message.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printContext() {
        ChatChannel currentChatChannel = channelsService.currentChannel();
        if (currentChatChannel != null && currentChatChannel.getJChannel() != null) {
            View view = currentChatChannel.getJChannel().getView();
            if (view != null) {
                printContext(nickName, currentChatChannel.rawName(), view.size());
            } else {
                printContext(nickName, currentChatChannel.rawName(), 0);
            }

        } else {
            printContext(nickName, "no channel!", 0);
        }
    }

    private static void printContext(String nickName, String channelName, int channelSize) {
        printContext(nickName, channelName, channelSize, "");
    }

    public static void printContext(String nickName, String channelName, int channelSize, String message) {
        System.out.printf(promptFormat() + "%s", nickName, channelName, channelSize, message);
    }

    public void commandMode(String userInput) {
        ICommand command = router.matchCommand(userInput);
        command.execute();
    }

    private static void printChatIntro() {
        System.out.println("\t\t\t##################################");
        System.out.println("\t\t\t#  Welcome to raduy's chat app!  #");
        System.out.println("\t\t\t##################################");
    }
}
