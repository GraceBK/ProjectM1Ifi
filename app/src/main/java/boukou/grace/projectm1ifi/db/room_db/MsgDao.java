package boukou.grace.projectm1ifi.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 25/04/2018.
 */
@Dao
public interface MsgDao {
    @Query("SELECT * FROM Msg WHERE numero_receiver = :phone")
    List<Msg> getAllMsgByNumber(String phone);

    @Query("SELECT cle FROM Msg WHERE name_receiver = :id_sms")
    String getKey(String id_sms);

    @Query("SELECT * FROM Msg WHERE numero_sender = 'sender'")
    LiveData<List<Msg>> getAll();

    @Query("UPDATE Msg SET status_sms = :statusSms WHERE name_receiver = :id_sms")
    void updateStatusSms(String id_sms, String statusSms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSms(Msg... msgs);
}
