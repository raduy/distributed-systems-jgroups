package pl.agh.edu.sr.raduy.channel;

import org.jgroups.JChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelsHandler {
    private final Map<ChannelName, JChannel> channels = new HashMap<>();
    private String nickName;

    public ChannelsHandler(String nickName) {
        this.nickName = nickName;
    }

    public void registerNewChannel(ChannelName name, JChannel channel) {
        channels.put(name, channel);
    }

    public String getNickName() {
        return nickName;
    }
}
