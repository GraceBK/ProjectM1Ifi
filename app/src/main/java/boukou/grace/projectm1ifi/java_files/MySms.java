package boukou.grace.projectm1ifi.java_files;

/**
 * boukou.grace.projectm1ifi.java_files
 * Created by grace on 13/04/2018.
 */
public class MySms {
    private int _id;
    private String _address;
    private String _msg;
    private String _readState;  // "0" pour SMS pas lu et "1" pour SMS lu
    private String _time;
    private String _folderName;


    private MyContact sender;

    public MySms() {}

    public MySms(MyContact sender, String msg) {
        this.sender = sender;
        this._msg = msg;
    }

    public MySms(String phone, String msg) {
        this._address = phone;
        this._msg = msg;
    }


    @Override
    public String toString() {
        return "SMS [id=" + get_id() + ", sender=" + get_address() + ", sms=" + get_msg() + "]";
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_msg() {
        return _msg;
    }

    public void set_msg(String _msg) {
        this._msg = _msg;
    }

    public String get_readState() {
        return _readState;
    }

    public void set_readState(String _readState) {
        this._readState = _readState;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String get_folderName() {
        return _folderName;
    }

    public void set_folderName(String _folderName) {
        this._folderName = _folderName;
    }

    public MyContact getSender() {
        return sender;
    }

    public void setSender(MyContact sender) {
        this.sender = sender;
    }
}
