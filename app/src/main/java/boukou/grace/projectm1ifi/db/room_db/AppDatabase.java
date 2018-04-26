package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 26/04/2018.
 */
@Database(entities = {RContact.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RContactDao rContactDao();
}
