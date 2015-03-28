package pl.edu.agh.dsrg.sr.chat.channel;

import org.jgroups.JChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelsHandler {
    private final Map<ChannelName, JChannel> channels = new HashMap<>();
    private JChannel currentChannel;
    private String nickName;

    public ChannelsHandler(String nickName) {
        this.nickName = nickName;
    }

    public void registerNewChannel(ChannelName name, JChannel channel) {
        if (channels.get(name) != null) {
            System.out.printf("Channel %s already exist! Joining...\n", name);
        } else {
            channels.put(name, channel);
        }
        switchChannel(name);
    }

    public String getNickName() {
        return nickName;
    }

    public void switchChannel(ChannelName channelName) {
        JChannel channel = channels.get(channelName);

        if (channel == null) {
            System.out.println("No such channel! Staying on last channel");
            return;
        }


        this.currentChannel = channel;
    }

    public JChannel currentChannel() {
        return this.currentChannel;
    }
}
