package pl.edu.agh.dsrg.sr.chat.channel;

import org.jgroups.Address;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class User {
    private Address srcAddress;
    private final String nickname;

    public User(Address srcAddress, String nickname) {
        this.srcAddress = srcAddress;
        this.nickname = nickname;
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    public Address getSrcAddress() {
        return srcAddress;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        if (srcAddress != null ? !srcAddress.equals(user.srcAddress) : user.srcAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = srcAddress != null ? srcAddress.hashCode() : 0;
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "srcAddress=" + srcAddress +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
