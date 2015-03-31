package pl.edu.agh.dsrg.sr.chat.domain.channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChatChannelRepository implements Iterable<ChatChannel> {
    private final Map<ChannelName, ChatChannel> channels = new ConcurrentHashMap<>();

    public void add(ChatChannel chatChannel) {
        this.channels.put(chatChannel.channelName(), chatChannel);
    }

    public ChatChannel loadByName(ChannelName name) {
        return channels.get(name);
    }

    public Set<ChatChannel> immutableView() {
        return Collections.unmodifiableSet(new HashSet<>(channels.values()));
    }

    @Override
    public Iterator<ChatChannel> iterator() {
        return this.immutableView().iterator();
    }
}
