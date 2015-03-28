package pl.edu.agh.dsrg.sr.chat.channel;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelName {
    private final InetAddress channelName;

    public ChannelName(String channelName) throws MalformedMulticastAddressException {
        this.channelName = validateChannelName(channelName);
    }

    private InetAddress validateChannelName(String channelName) {
        try {
            InetAddress address = Inet4Address.getByName(channelName);

            if (!address.isMulticastAddress()) {
                throw new MalformedMulticastAddressException(channelName);
            }
            return address;
        } catch (UnknownHostException e) {
            throw new MalformedMulticastAddressException(channelName);
        }
    }

    @Override
    public String toString() {
        return channelName.getCanonicalHostName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelName that = (ChannelName) o;

        if (channelName != null ? !channelName.equals(that.channelName) : that.channelName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return channelName != null ? channelName.hashCode() : 0;
    }
}
