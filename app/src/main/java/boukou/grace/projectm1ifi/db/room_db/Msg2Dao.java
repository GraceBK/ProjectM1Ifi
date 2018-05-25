package boukou.grace.projectm1ifi.db.room_db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * boukou.grace.projectm1ifi.db.room_db
 * Created by grace on 19/05/2018.
 */
@Dao
public interface Msg2Dao {
    @Query("SELECT * FROM Msg2")
    List<Msg> getAllMsg_2();

    @Query("SELECT * FROM Msg2 WHERE numero_receiver = :phone")
    List<Msg> getAllMsgByNumber_2(String phone);

    @Query("SELECT cle FROM Msg2 WHERE name_receiver = :id_sms")
    String getKey_2(String id_sms);

    @Query("SELECT * FROM Msg2")
    LiveData<List<Msg>> getAll2();

    @Query("UPDATE Msg2 SET status_sms = :statusSms WHERE name_receiver = :id_sms")
    int updateStatusSms_2(String id_sms, String statusSms);

    @Query("UPDATE Msg2 SET cle = :cle WHERE name_receiver = :id_sms")
    int updateKeySms_2(String id_sms, String cle);

    @Query("UPDATE Msg2 SET cle = :cle WHERE name_receiver = :id_sms")
    void updateKeySms_22(String id_sms, String cle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSms_2(Msg2... msgs);
}
