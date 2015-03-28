package pl.agh.edu.sr.raduy.channel;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class ChannelName {
    private final String channelName;

    public ChannelName(String channelName) throws MalformedMulticastAddressException {
        validateChannelName(channelName);
        this.channelName = channelName;
    }

    private void validateChannelName(String channelName) {
        try {
            InetAddress address = Inet4Address.getByName(channelName);

            if (!address.isMulticastAddress()) {
                throw new MalformedMulticastAddressException(channelName);
            }
        } catch (UnknownHostException e) {
            throw new MalformedMulticastAddressException(channelName);
        }
    }

    @Override
    public String toString() {
        return channelName;
    }
}
