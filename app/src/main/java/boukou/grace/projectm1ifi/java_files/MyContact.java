package boukou.grace.projectm1ifi.java_files;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * boukou.grace.projectm1ifi.java_files
 * Created by grace on 11/04/2018.
 */
public class MyContact implements Comparator<String> {

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

    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.equals(o2)) {
            return 0;
        }
        return o1.compareTo(o2);
    }
}
