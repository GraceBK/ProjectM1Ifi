package boukou.grace.projectm1ifi.java_files;

/**
 * boukou.grace.projectm1ifi.java_files
 * Created by grace on 11/04/2018.
 */
public class MyContact {

    private String username;
    private String phone_number;

    public MyContact(String username, String phone_number) {
        this.username = username;
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "nom:" + getUsername() + " phone=" + getPhone_number();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
