package boukou.grace.projectm1ifi;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.MyMessage;
import boukou.grace.projectm1ifi.db.room_db.Sms;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 25/04/2018.
 */
public class MyMessageViewModel extends AndroidViewModel {

    private MyMessage message;

    private LiveData<List<Sms>> smsList;

    public MyMessageViewModel(@NonNull Application application) {
        super(application);

        message = MyMessage.getDatabase(this.getApplication());

        smsList = message.smsDao().getAll();
    }

    public LiveData<List<Sms>> getSmsList() {
        return smsList;
    }

    public void update(List<Sms> sms) {
        Objects.requireNonNull(smsList.getValue()).clear();
        smsList.getValue().addAll(sms);
    }
}
