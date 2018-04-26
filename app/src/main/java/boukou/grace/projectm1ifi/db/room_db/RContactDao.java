package boukou.grace.projectm1ifi.db.room_db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 26/04/2018.
 */
@Dao
public interface RContactDao {
    @Query("SELECT * FROM rcontact")
    List<RContact> getAllRContacts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRContact(RContact... contacts);
}
