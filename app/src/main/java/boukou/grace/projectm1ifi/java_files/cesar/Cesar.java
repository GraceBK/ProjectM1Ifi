package boukou.grace.projectm1ifi.java_files.cesar;

/**
 * boukou.grace.projectm1ifi.java_files.cesar
 * Created by grace on 11/04/2018.
 */
public class Cesar {

    public static String crypter(int cle, String msg) {
        StringBuilder res = new StringBuilder();
        char c;
        for (int i = 0; i < msg.length(); ++i) {
            c = msg.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c + cle);
                if (c > 'z') {
                    c = (char) (c - 'z' + 'a' - 1);
                }
                res.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                c = (char) (c + cle);
                if (c > 'Z') {
                    c = (char) (c - 'Z' + 'A' - 1);
                }
                res.append(c);
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

    public static String decrypter(int cle, String msg) {
        StringBuilder res = new StringBuilder();
        char c;
        for (int i = 0; i < msg.length(); ++i) {
            c = msg.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c - cle);
                if (c < 'a') {
                    c = (char) (c + 'z' - 'a' + 1);
                }
                res.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                c = (char) (c - cle);
                if (c < 'A') {
                    c = (char) (c + 'Z' - 'A' + 1);
                }
                res.append(c);
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }
}
