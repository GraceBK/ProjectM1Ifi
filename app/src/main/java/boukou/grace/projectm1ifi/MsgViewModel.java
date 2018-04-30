package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
public class MsgViewModel extends AndroidViewModel {

    private AppDatabase db;

    private LiveData<List<Msg>> msgList;
    private LiveData<List<Msg>> msgListByPhone;

    public MsgViewModel(@NonNull Application application) {
        super(application);

        db = AppDatabase.getDatabase(this.getApplication());
        msgList = db.msgDao().getAll();
        // msgListByPhone = db.msgDao().getAllMsgByNumber()
        //msgListByPhone = Transformations.switchMap(fil)
        //msgList = db.msgDao().getAllMsgByNumber("0680728051");
        //msgList = (LiveData<List<Msg>>) db.msgDao().getAllMsgByNumber("0680728051");
    }

    @SuppressLint("StaticFieldLeak")
    public LiveData<List<Msg>> getMsgByPhone(final String phone) {
        Msg msg = new Msg();
        new AsyncTask<Msg, Void, Void>() {

            @Override
            protected Void doInBackground(Msg... msgs) {
                //for (Msg msg1 : msgs) {
                db.msgDao().getAllMsgByNumber(phone);
                //}
                return null;
            }
        }.execute(msg);
        return msgListByPhone;
    }

    public void update(List<Msg> msgs) {
        Objects.requireNonNull(msgList.getValue()).clear();
        msgList.getValue().addAll(msgs);
    }

    @SuppressLint("StaticFieldLeak")
    public void addMsg(String receiver, String msg_crypt, String msg_decrypt, String cle) {
        Msg msg = new Msg();
        msg.phoneReceiver = receiver;
        msg.sms1 = msg_crypt;
        msg.sms2 = msg_decrypt;
        msg.key = cle;
        new AsyncTask<Msg, Void, Void>() {

            @Override
            protected Void doInBackground(Msg... msgs) {
                db.msgDao().insertSms(msgs);
                return null;
            }
        }.execute(msg);
    }

    public LiveData<List<Msg>> getMsgList() {
        return msgList;
    }

    public LiveData<List<Msg>> getMsgListByPhone() {
        return msgListByPhone;
    }
}
