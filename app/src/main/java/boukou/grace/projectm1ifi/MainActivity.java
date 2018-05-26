package boukou.grace.projectm1ifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.RContact;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private final int PICK_CONTACT_REQUEST = 1; // Le code de reponse

    RecyclerView recyclerView;
    private RecentAdapter adapter;
    List<RContact> contacts;
    RecentViewModel viewModel;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkAndRequestPermissions()) {}

        db = AppDatabase.getDatabase(getApplicationContext());

        contacts = db.rContactDao().getAllRContacts();

        recyclerView = findViewById(R.id.main_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentAdapter(contacts);
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(RecentViewModel.class);
        viewModel.getContactList().observe(this, new Observer<List<RContact>>() {
            @Override
            public void onChanged(@Nullable List<RContact> rContacts) {
                adapter.update(rContacts);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

        FloatingActionButton fab_receive = findViewById(R.id.fab_receive);
        fab_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReceiveSMSActivity.class);
                startActivity(intent);
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
            @SuppressLint("Recycle") Cursor cursor = getContentResolver()
                    .query(Objects.requireNonNull(contactUri), null, null, null, null);
            Objects.requireNonNull(cursor).moveToFirst();

            int username_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            int phone_id = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String name = cursor.getString(username_id);
            String number = cursor.getString(phone_id);

            RContact rContact = new RContact();
            rContact.setUsername(name);
            rContact.setPhone(number);

            new AsyncTask<RContact, Void, Void>() {
                @Override
                protected Void doInBackground(RContact... contacts) {
                    for (RContact contact : contacts) {
                        db.rContactDao().insertRContact(contact);
                    }
                    return null;
                }
            }.execute(rContact);

            final Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("USERNAME", name);
            intent.putExtra("PHONE", number);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}