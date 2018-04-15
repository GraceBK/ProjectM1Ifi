package boukou.grace.projectm1ifi.java_files;

/**
 * boukou.grace.projectm1ifi.java_files
 * Created by grace on 14/04/2018.
 */
public class MyDiscussion extends MyContact {

    private String description;

    public MyDiscussion(String username, String phone_number, String description) {
        super(username, phone_number);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
