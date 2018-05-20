package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 19/05/2018.
 */
@Entity
public class Msg2 {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    /*public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";*/

    @ColumnInfo(name = "name_receiver")
    public String nameReceiver;

    @ColumnInfo(name = "numero_receiver")
    public String phoneReceiver;

    @ColumnInfo(name = "numero_sender")
    public String phoneSender;

    @ColumnInfo(name = "sms_crypt")
    public String sms1;

    @ColumnInfo(name = "sms_decrypt")
    public String sms2;

    @ColumnInfo(name = "cle")
    public String key;

    @ColumnInfo(name = "status_sms")
    public String status_sms;

    @ColumnInfo(name = "status_key")
    public String status_key;

    @Override
    public String toString() {
        String res;
        res = uid +" "+ nameReceiver +" "+ phoneReceiver +" "+ phoneSender +" "+ sms1 +" "+ sms2 +" "+ key;
        return res;
    }
}
