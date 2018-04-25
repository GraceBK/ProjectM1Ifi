package boukou.grace.projectm1ifi.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 25/04/2018.
 */
@Dao
public interface SmsDao {
    @Query("SELECT * FROM sms")
    LiveData<List<Sms>> getAll();

    @Query("SELECT sms.name_receiver, sms.numero_receiver FROM sms WHERE uid IN (:smsIds)")
    List<Sms> getAllSmsByIds(int[] smsIds);

    @Insert
    public void insertSms(Sms sms);
}
