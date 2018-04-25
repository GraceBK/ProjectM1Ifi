package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 25/04/2018.
 */
@Database(entities = {Sms.class}, version = 1)
public abstract class MyMessage extends RoomDatabase {
    public abstract SmsDao smsDao();

    private static MyMessage INSTANCE;

    public static MyMessage getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), MyMessage.class, "mymessage_db")
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }
}
