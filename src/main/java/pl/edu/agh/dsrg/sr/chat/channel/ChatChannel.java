package pl.edu.agh.dsrg.sr.chat.channel;

import org.jgroups.Address;
import org.jgroups.JChannel;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannel {
    private final String channelRawName;
    private final JChannel jChannel;
    private final Set<User> users = new HashSet<>();

    public ChatChannel(String channelRawName, JChannel jChannel) {
        this.channelRawName = channelRawName;
        this.jChannel = jChannel;
    }

    public void connectUser(User user) {
        this.users.add(user);
    }

    public void disconnectUser(User user) {
        this.users.remove(user);
    }

    public JChannel getJChannel() {
        return jChannel;
    }

    public Set<User> getUsers() {
        return new HashSet<>(users);
    }

    public User findUser(Address srcAddress) {
        for (User user : users) {
            if (user.getSrcAddress().equals(srcAddress)) {
                return user;
            }
        }

        throw new NoSuchElementException(format("No user with %s srcAddress in %s channel", srcAddress, jChannel.getClusterName()));
    }

    public void connectMe() {
        if (jChannel.isConnected()) {
            return;
        }

        try {
            jChannel.connect(channelRawName);
        } catch (Exception e) {
            System.out.printf("Cannot connect to %s channel", channelRawName);
            e.printStackTrace();
        }
    }
}
