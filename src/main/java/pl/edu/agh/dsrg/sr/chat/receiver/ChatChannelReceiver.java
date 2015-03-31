package pl.edu.agh.dsrg.sr.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import pl.edu.agh.dsrg.sr.chat.ChatApp;
import pl.edu.agh.dsrg.sr.chat.domain.User;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChannelName;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannel;
import pl.edu.agh.dsrg.sr.chat.domain.channel.ChatChannelRepository;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannelReceiver extends ReceiverAdapter {
    private final JChannel jChannel;
    private final String nickName;
    private final ChannelName channelName;
    private final ChatChannelRepository repository;

    public ChatChannelReceiver(JChannel jChannel, String nickName, ChannelName channelName, ChatChannelRepository repository) {
        this.jChannel = jChannel;
        this.nickName = nickName;
        this.channelName = channelName;
        this.repository = repository;
    }

    @Override
    public void viewAccepted(View newView) {
        ChatChannel chatChannel = repository.loadByName(channelName);

        Set<User> newUsersView = mapToUser(newView);
        chatChannel.updateUsers(newUsersView);

        System.out.println("<Logger>: View changed: " + newView);
    }

    private Set<User> mapToUser(View newView) {
        HashSet<User> newUsersView = new HashSet<>();
        for (Address address : newView) {
            newUsersView.add(new User(jChannel.getName(address)));
        }
        return newUsersView;
    }

    @Override
    public void receive(Message msg) {
        if (isMyMessage(msg)) {
            return;
        }

        String message;
        Address srcAddress = msg.getSrc();
        try {
            message = ChatOperationProtos.ChatMessage.parseFrom(msg.getBuffer()).getMessage();

            String nickName = jChannel.getName(srcAddress);
            int membersInChannelCount = jChannel.getView().size();

            printMessage(message, nickName, membersInChannelCount);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void printMessage(String message, String nickName, int membersInChannelCount) {
        System.out.println();
        ChatApp.printContext(nickName, channelName.toString(), membersInChannelCount, message);
        System.out.println();
        ChatApp.printContext();
    }

    private boolean isMyMessage(Message msg) {
        return jChannel.getName(msg.getSrc()).equals(nickName);
    }
}
