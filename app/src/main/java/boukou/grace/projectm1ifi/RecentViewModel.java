package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Observable;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.RContact;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
public class RecentViewModel extends AndroidViewModel {

    private AppDatabase db;

    private LiveData<List<RContact>> contactList;

    public RecentViewModel(@NonNull Application application) {
        super(application);

        db = AppDatabase.getDatabase(this.getApplication());
        contactList = db.rContactDao().getAll();
    }

    @SuppressLint("StaticFieldLeak")
    public void addRContact(String username, String phone) {
        RContact rContact = new RContact();
        rContact.setUsername(username);
        rContact.setPhone(phone);

        new AsyncTask<RContact, Void, Void>() {
            @Override
            protected Void doInBackground(RContact... rContacts) {
                db.rContactDao().insertRContact(rContacts);
                return null;
            }
        }.execute(rContact);

    }

    public void update(List<RContact> rContacts) {
        Objects.requireNonNull(contactList.getValue()).clear();
        contactList.getValue().addAll(rContacts);
    }

    public LiveData<List<RContact>> getContactList() {
        return contactList;
    }
}
