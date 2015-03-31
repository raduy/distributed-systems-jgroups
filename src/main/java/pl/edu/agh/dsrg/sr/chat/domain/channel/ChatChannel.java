package pl.edu.agh.dsrg.sr.chat.domain.channel;

import org.jgroups.JChannel;
import pl.edu.agh.dsrg.sr.chat.domain.User;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannel {
    private final ChannelName channelName;
    private final JChannel jChannel;
    private final Set<User> users = new HashSet<>();

    public ChatChannel(ChannelName channelName, JChannel jChannel) {
        this.channelName = channelName;
        this.jChannel = jChannel;
    }

    public void connectUser(User user) {
        this.users.add(user);
    }

    public void disconnectUser(User user) {
        this.users.remove(user);
    }

    public String rawName() {
        return channelName.toString();
    }

    public void connectMe() {
        if (jChannel.isConnected()) {
            return;
        }

        try {
            jChannel.setName("ppp");
            jChannel.connect(channelName.toString());
        } catch (Exception e) {
            System.out.printf("Cannot connect to %s channel", channelName);
            e.printStackTrace();
        }
    }

    public boolean amIConnected() {
        return this.getJChannel().isConnected();
    }

    public JChannel getJChannel() {
        return jChannel;
    }

    public Set<User> getUsers() {
        return new HashSet<>(users);
    }

    public ChannelName channelName() {
        return channelName;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append(channelName)
                .append(format(" (%s)\n", amIConnected() ? "connected" : "disconnected"));

        for (User user : users) {
            buffer.append(format("\t\t%s \n", user.getNickname()));
        }

        return buffer.toString();
    }
}
