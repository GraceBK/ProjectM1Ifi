package boukou.grace.projectm1ifi.archive;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.Msg;
import boukou.grace.projectm1ifi.db.room_db.MyMessage;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 25/04/2018.
 */
public class MyMessageViewModel extends AndroidViewModel {

    private MyMessage message;

    private LiveData<List<Msg>> smsList;

    public MyMessageViewModel(@NonNull Application application) {
        super(application);

        message = MyMessage.getDatabase(this.getApplication());

        smsList = message.msgDao().getAll();
    }

    public LiveData<List<Msg>> getSmsList() {
        return smsList;
    }

    @SuppressLint("StaticFieldLeak")
    public void addSms(String phoneSender, String phoneReceiver, final String sms, String key) {
        Msg myMsg = new Msg();
        myMsg.key = key;
        myMsg.nameReceiver = phoneReceiver;
        myMsg.phoneSender = phoneSender;
        myMsg.sms1 = sms;

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... mySms) {
                message.msgDao().insertSms(mySms);
                return null;
            }
        }.execute(myMsg);
    }

    public void update(List<Msg> sms) {
        Objects.requireNonNull(smsList.getValue()).clear();
        smsList.getValue().addAll(sms);
    }
}
