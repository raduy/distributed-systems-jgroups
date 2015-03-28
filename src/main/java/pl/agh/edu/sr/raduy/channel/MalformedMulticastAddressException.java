package pl.agh.edu.sr.raduy.channel;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class MalformedMulticastAddressException extends RuntimeException {
    public MalformedMulticastAddressException(String address) {
        super(String.format("Address: %s it's not a multicast address!", address));
    }
}
