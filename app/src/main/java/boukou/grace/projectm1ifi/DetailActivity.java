package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

//    SmsSQLiteOpenHelper sqLiteOpenHelper;

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

//        msgList = db.msgDao().getAllMsg();

        String[] status_sms = getResources().getStringArray(R.array.status_sms);


        name = getIntent().getStringExtra("USERNAME");
        phone = getIntent().getStringExtra("PHONE");

        getSupportActionBar().setTitle(name);
        msgList = db.msgDao().getAllMsgByNumber(phone);

        Log.e("GGG", ""+taille);

        sms = findViewById(R.id.edit_txt_send);

        Msg msg = new Msg();
        msg.phoneReceiver = phone;

        // TODO : DB
        //sqLiteOpenHelper = new SmsSQLiteOpenHelper(this);

        //smsList = sqLiteOpenHelper.getSmsByPhoneNumber(phone);


//        msg.nameReceiver = phone + "_" + msgList.size();



        mySmsRecycler = findViewById(R.id.container_sms);
        mySmsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MsgAdapter(msgList);

        mySmsRecycler.smoothScrollToPosition(msgList.size());

        mySmsRecycler.setAdapter(adapter);




        viewModel = ViewModelProviders.of(this).get(MsgViewModel.class);
        viewModel.getMsgList().observe(this, /*new Observer<List<Msg>>() {
            @Override
            public void onChanged(@Nullable List<Msg> msgs) {
                adapter.update(msgs);
            }
        }*/msgs -> {
            new AsyncTask<Msg, Void, Void>() {

                @Override
                protected Void doInBackground(Msg... msgs) {
                    final List<Msg> msgs1 = db.msgDao().getAllMsgByNumber(phone);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.update(msgs1);
                            //taille = msgList.size();
                        }
                    });
                    return null;
                }
            }.execute(msg);
        });

        FloatingActionButton fab = findViewById(R.id.fab_send);
        fab.setOnClickListener(view -> {
         //   Log.e("rrrr", sms.getText().toString() + " " + phone);
            if (!sms.getText().toString().isEmpty()) {
                int cle = Cesar.generateKey();
                taille = adapter.getItemCount();
                // TODO Save avant de send
                sms_clair = sms.getText().toString();
                sms_chiffre = Cesar.crypter(cle, sms_clair);
               // sendSMS(sms_chiffre, phone + "_" + taille/*msgList.size()*//*msg.nameReceiver // getSize(phone)*/);
                // DONE Action envoye
                // Deplacer cette fonction
                //sqLiteOpenHelper.addSMS(new MySms(phone, sms_clair, ""+cle, sender));
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


                sendSMS(msg.sms1, phone + "_" + taille/*msgList.size()*//*msg.nameReceiver // getSize(phone)*/);


                sms.setText("");



            }
        });
    }

    protected void sendKey(String key, String id_sms_send) {
        try {
            SmsManager smsKEY = SmsManager.getDefault();
            smsKEY.sendTextMessage(phone, null, "MY_APP_KEY " + id_sms_send + " " + key, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendSMS(String msg, String id_sms_send) {
        try {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "MY_APP_SMS " + id_sms_send + " " + msg, null, null/*sentPendingIntent, deliveredPendingIntent*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
