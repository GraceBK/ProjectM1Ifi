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
    @Query("SELECT * FROM Msg")
    List<Msg> getAllMsg();

    @Query("SELECT * FROM Msg WHERE numero_receiver = :phone")
    List<Msg> getAllMsgByNumber(String phone);

    @Query("SELECT * FROM Msg")
    LiveData<List<Msg>> getAll();

    //@Query("SELECT sms.name_receiver, sms.numero_receiver FROM sms WHERE uid IN (:smsIds)")
    //List<Msg> getAllSmsByIds(int[] smsIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSms(Msg... msgs);
}
