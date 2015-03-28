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
}
