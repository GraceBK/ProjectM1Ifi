package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;
import boukou.grace.projectm1ifi.java_files.cesar.Cesar;

public class DetailActivity extends AppCompatActivity {

    RecyclerView mySmsRecycler;
    MsgAdapter adapter;
    MsgViewModel viewModel;

    List<Msg> msgList;

    private AppDatabase db;

    String name;
    String phone;
    String sms_clair;
    String sms_chiffre;
    String sender = "sender";

    TextView sms;

    int taille;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = AppDatabase.getDatabase(getApplicationContext());

        name = getIntent().getStringExtra("USERNAME");
        phone = getIntent().getStringExtra("PHONE");

        getSupportActionBar().setTitle(name);
        msgList = db.msgDao().getAllMsgByNumber(phone);

        sms = findViewById(R.id.edit_txt_send);

        Msg msg = new Msg();
        msg.phoneReceiver = phone;

        mySmsRecycler = findViewById(R.id.container_sms);
        mySmsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MsgAdapter(msgList);

        mySmsRecycler.smoothScrollToPosition(msgList.size());

        mySmsRecycler.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MsgViewModel.class);
        viewModel.getMsgList().observe(this, msgs -> {
            new AsyncTask<Msg, Void, Void>() {

                @Override
                protected Void doInBackground(Msg... msgs) {
                    final List<Msg> msgs1 = db.msgDao().getAllMsgByNumber(phone);
                    runOnUiThread(() -> adapter.update(msgs1));
                    return null;
                }
            }.execute(msg);
        });

        FloatingActionButton fab = findViewById(R.id.fab_send);
        fab.setOnClickListener(view -> {
            if (!sms.getText().toString().isEmpty()) {
                int cle = Cesar.generateKey();
                taille = adapter.getItemCount();
                sms_clair = sms.getText().toString();
                sms_chiffre = Cesar.crypter(cle, sms_clair);
                
                msg.nameReceiver = phone + "_" + taille;
                msg.phoneSender = sender;
                msg.sms1 = sms_chiffre;
                msg.key = ""+cle;

                new AsyncTask<Msg, Void, Void>() {

                    @Override
                    protected Void doInBackground(Msg... msgs) {
                        for (Msg msg1 : msgs) {
                            db.msgDao().insertSms(msg1);
                        }
                        return null;
                    }
                }.execute(msg);

                sendSMS(msg.sms1, phone + "_" + taille);

                sms.setText("");
            }
        });
    }

    protected void sendSMS(String msg, String id_sms_send) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "iSMS " + id_sms_send + " " + msg, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
