package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.adapter_files.MyMessageAdapter;
import boukou.grace.projectm1ifi.db.room_db.MyMessage;
import boukou.grace.projectm1ifi.db.room_db.RContact;
import boukou.grace.projectm1ifi.db.room_db.Sms;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final int PICK_CONTACT_REQUEST = 1; // Le code de reponse

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ArrayList<RContact> contacts = new ArrayList<>();

    private MyMessage db;
    private MyMessageViewModel viewModel;
    private MyMessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = MyMessage.getDatabase(getApplicationContext());

        recyclerView = findViewById(R.id.main_recycler_view);

        for (int i = 0; i < 100; i++) {
            RContact contact = new RContact("Grace " + i, "098778978", "send");
            contacts.add(contact);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentAdapter(contacts);
        recyclerView.setAdapter(adapter);

        /*viewModel = ViewModelProviders.of(this).get(MyMessageViewModel.class);
        viewModel.getSmsList().observe(this, new Observer<List<Sms>>() {
            @Override
            public void onChanged(@Nullable List<Sms> sms) {
                messageAdapter.update(sms);
            }
        });*/
        //viewModel.addSms(sms1.nameReceiver, sms1.phoneReceiver, sms1.sms1, sms1.key);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });
    }

    /**
     * Start the Activity
     */
    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    /**
     * Receive the Result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            getContact(data);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void getContact(Intent data) {
        try {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            @SuppressLint("Recycle") Cursor cursor = getContentResolver()
                    .query(Objects.requireNonNull(contactUri), null, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();

            int username_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            int phone_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(username_id);
            String number = cursor.getString(phone_id);
            // DONE ajout d'une discussion

            Sms sms_ = new Sms();
            sms_.nameReceiver = name;
            sms_.phoneReceiver = number;

            new AsyncTask<Sms, Void, Void>() {
                @Override
                protected Void doInBackground(Sms... sms) {
                    for (Sms sms1 : sms) {
                        db.smsDao().insertSms(sms1);
                    }
                    return null;
                }
            }.execute(sms_);

            Log.e(TAG, name + " " + number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}