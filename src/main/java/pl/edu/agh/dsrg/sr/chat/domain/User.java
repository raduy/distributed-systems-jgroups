package pl.edu.agh.dsrg.sr.chat.domain;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class User {
    private final String nickname;

    public User(String nickname) {
        this.nickname = nickname;
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

        return true;
    }

    @Override
    public int hashCode() {
        return nickname != null ? nickname.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
