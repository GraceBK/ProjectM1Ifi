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

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveredBroadcastReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

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
        Log.e("GGG", ""+msgList.size());

        sms = findViewById(R.id.edit_txt_send);
        //current_time = findViewById(R.id.e);

        // TODO : DB
        //sqLiteOpenHelper = new SmsSQLiteOpenHelper(this);

        //smsList = sqLiteOpenHelper.getSmsByPhoneNumber(phone);

        Msg msg = new Msg();
        msg.nameReceiver = phone + "_" + msgList.size();
        msg.phoneReceiver = phone;




        mySmsRecycler = findViewById(R.id.container_sms);
        mySmsRecycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MsgAdapter(msgList);

        mySmsRecycler.smoothScrollToPosition(msgList.size());
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mySmsRecycler.scrollToPosition(msgList.size());
            }
        }, 200);*/
        mySmsRecycler.setAdapter(adapter);


        viewModel = ViewModelProviders.of(this).get(MsgViewModel.class);
        viewModel.getMsgList().observe(this, msgs -> {
            new AsyncTask<Msg, Void, Void>() {

                @Override
                protected Void doInBackground(Msg... msgs) {
                    final List<Msg> msgs1 = db.msgDao().getAllMsgByNumber(phone);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.update(msgs1);
                        }
                    });
                    return null;
                }
            }.execute(msg);
        });



        /**
         * DONE J'enregistre les receivers ici car dans sendSMS y a deux erreur du type:
         * * Pour l'accuse d'envoi
         *      E/ActivityThread: Activity boukou.grace.projectm1ifi.DetailActivity has leaked IntentReceiver boukou.grace.projectm1ifi.DetailActivity$3@892dd21 that was originally registered here. Are you missing a call to unregisterReceiver()?
         * * Pour l'accuse de reception
         *      E/ActivityThread: Activity boukou.grace.projectm1ifi.DetailActivity has leaked IntentReceiver boukou.grace.projectm1ifi.DetailActivity$2@a3f2f88 that was originally registered here. Are you missing a call to unregisterReceiver()?
         *
         * Ensuite dans onStop on desinscrit les receivers de l'activite
         *
         * mais pas de quoi faire planter l'APP.
         *
         * SOLUTION SUR : https://stackoverflow.com/questions/9078390/intentrecieverleakedexception-are-you-missing-a-call-to-unregisterreceiver
         */
        // Quand le SMS a ete envoye
        sendBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS envoyé", Toast.LENGTH_SHORT).show();
                        // DONE : Le SMS est envoye je mets ajour le status des sms
                        msg.status_sms = status_sms[0];

                        new AsyncTask<Msg, Void, Void>() {

                            @Override
                            protected Void doInBackground(Msg... msgs) {
                                for (Msg msg1 : msgs) {
                                    db.msgDao().updateStatusSms(msg1.nameReceiver, msg1.status_sms);
                                }
                                return null;
                            }
                        }.execute(msg);
                        // TODO A supprimer
                        // DONE : Le SMS est envoye je mets ajour le status des sms
                        msg.status_sms = status_sms[1];

                        new AsyncTask<Msg, Void, Void>() {

                            @Override
                            protected Void doInBackground(Msg... msgs) {
                                for (Msg msg1 : msgs) {
                                    db.msgDao().updateStatusSms(msg1.nameReceiver, msg1.status_sms);
                                }
                                return null;
                            }
                        }.execute(msg);

                        // TODO : send the key message
                        sendKey(db.msgDao().getKey(msg.nameReceiver), msg.nameReceiver);
                        // TODO A supprimer
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // TODO : action a faire si SMS non livré
                        Toast.makeText(getBaseContext(), "SMS non livré", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // TODO : action a faire si Pas de service
                        Toast.makeText(getBaseContext(), "Pas de service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // TODO : action a faire si PDU nul
                        Toast.makeText(getBaseContext(), "PDU nul", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // TODO : action a faire si Radio éteinte
                        Toast.makeText(getBaseContext(), "Radio éteinte", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        // Quand le SMS a ete livre
        deliveredBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS livré", Toast.LENGTH_SHORT).show();
                        // DONE : Le SMS est envoye je mets ajour le status des sms
                        msg.status_sms = status_sms[1];

                        new AsyncTask<Msg, Void, Void>() {

                            @Override
                            protected Void doInBackground(Msg... msgs) {
                                for (Msg msg1 : msgs) {
                                    db.msgDao().updateStatusSms(msg1.nameReceiver, msg1.status_sms);
                                }
                                return null;
                            }
                        }.execute(msg);

                        // TODO : send the key message
                        sendKey(db.msgDao().getKey(msg.nameReceiver), msg.nameReceiver);

                        Toast.makeText(getBaseContext(), "la cle ? "+db.msgDao().getKey(msg.nameReceiver), Toast.LENGTH_SHORT).show();

                        break;
                    case Activity.RESULT_CANCELED:
                        // TODO : action a faire si SMS non livré
                        Toast.makeText(getBaseContext(), "SMS non livré", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveredBroadcastReceiver, new IntentFilter(DELIVERED));

        FloatingActionButton fab = findViewById(R.id.fab_send);
        fab.setOnClickListener(view -> {
         //   Log.e("rrrr", sms.getText().toString() + " " + phone);
            if (!sms.getText().toString().isEmpty()) {
                int cle = Cesar.generateKey();
                // TODO Save avant de send
                sms_clair = sms.getText().toString();
                sms_chiffre = Cesar.crypter(cle, sms_clair);
                sendSMS(sms_chiffre, msg.nameReceiver);
                // DONE Action envoye
                // Deplacer cette fonction
                //sqLiteOpenHelper.addSMS(new MySms(phone, sms_clair, ""+cle, sender));
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
            smsManager.sendTextMessage(phone, null, "MY_APP_SMS " + id_sms_send + " " + msg, sentPendingIntent, deliveredPendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (sendBroadcastReceiver != null || deliveredBroadcastReceiver != null) {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveredBroadcastReceiver);
        }
        sendBroadcastReceiver = null;
        deliveredBroadcastReceiver = null;
        //unregisterReceiver(sendBroadcastReceiver);
        //unregisterReceiver(deliveredBroadcastReceiver);
        super.onStop();
    }

}
