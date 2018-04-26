package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
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

    @SuppressLint("StaticFieldLeak")
    public void addSms(String phoneSender, String phoneReceiver, final String sms, String key) {
        Sms mySms = new Sms();
        mySms.key = key;
        mySms.nameReceiver = phoneReceiver;
        mySms.phoneSender = phoneSender;
        mySms.sms1 = sms;

        new AsyncTask<Sms, Void, Void>() {
            @Override
            protected Void doInBackground(Sms... mySms) {
                message.smsDao().insertSms(mySms);
                return null;
            }
        }.execute(mySms);
    }

    public void update(List<Sms> sms) {
        Objects.requireNonNull(smsList.getValue()).clear();
        smsList.getValue().addAll(sms);
    }
}
