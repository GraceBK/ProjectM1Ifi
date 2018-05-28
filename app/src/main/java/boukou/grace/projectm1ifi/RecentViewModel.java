package boukou.grace.projectm1ifi;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.RContact;

/**
 * boukou.grace.projectm1ifi
 * Created by grace on 26/04/2018.
 */
public class RecentViewModel extends AndroidViewModel {

    private LiveData<List<RContact>> contactList;

    public RecentViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(this.getApplication());
        contactList = db.rContactDao().getAll();
    }

    public LiveData<List<RContact>> getContactList() {
        return contactList;
    }
}
