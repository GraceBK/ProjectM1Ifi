package boukou.grace.projectm1ifi;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
public class MsgViewModel extends AndroidViewModel {

    private LiveData<List<Msg>> msgList;

    public MsgViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        msgList = db.msgDao().getAll();
    }

    public LiveData<List<Msg>> getMsgList() {
        return msgList;
    }

}
