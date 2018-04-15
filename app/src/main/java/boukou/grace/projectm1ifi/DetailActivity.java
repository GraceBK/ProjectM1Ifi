package boukou.grace.projectm1ifi;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.adapter_files.MySmsAdapter;
import boukou.grace.projectm1ifi.db.SmsSQLiteOpenHelper;
import boukou.grace.projectm1ifi.java_files.MyContact;
import boukou.grace.projectm1ifi.java_files.MySms;
import boukou.grace.projectm1ifi.java_files.cesar.Cesar;

public class DetailActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveredBroadcastReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    private RecyclerView mySmsRecycler;
    private MySmsAdapter mySmsAdapter;

    private List<MySms> smsList = new ArrayList<>();

    String name;
    String phone;

    TextView sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // TODO : DB
        SmsSQLiteOpenHelper sqLiteOpenHelper = new SmsSQLiteOpenHelper(this);

        MySms mms = new MySms("0987654321", "COUCOU");

        MySms mms2 = new MySms("0650231529", "COUCOUhjvjiguyb  khk");


        sqLiteOpenHelper.addSMS(mms);
        sqLiteOpenHelper.addSMS(mms2);
        sqLiteOpenHelper.getAllSMS();
        sqLiteOpenHelper.getSmsByPhoneNumber("0650231529");
        prepareSMS();

        mySmsRecycler = findViewById(R.id.container_sms);
        mySmsRecycler.setLayoutManager(new LinearLayoutManager(this));

        mySmsAdapter = new MySmsAdapter(smsList);
        mySmsRecycler.smoothScrollToPosition(smsList.size());
        mySmsRecycler.setAdapter(mySmsAdapter);



        /*
          J'enregistre les receivers ici car dans sendSMS y a deux erreur du type:
           * Pour l'accuse d'envoi
                E/ActivityThread: Activity boukou.grace.projectm1ifi.DetailActivity has leaked IntentReceiver boukou.grace.projectm1ifi.DetailActivity$3@892dd21 that was originally registered here. Are you missing a call to unregisterReceiver()?
           * Pour l'accuse de reception
                E/ActivityThread: Activity boukou.grace.projectm1ifi.DetailActivity has leaked IntentReceiver boukou.grace.projectm1ifi.DetailActivity$2@a3f2f88 that was originally registered here. Are you missing a call to unregisterReceiver()?

             Ensuite dans onStop on desinscrit les receivers de l'activite

          mais pas de quoi faire planter l'APP.

          SOLUTION SUR : https://stackoverflow.com/questions/9078390/intentrecieverleakedexception-are-you-missing-a-call-to-unregisterreceiver
         */
        // Quand le SMS a ete envoye
        sendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS envoyé", Toast.LENGTH_SHORT).show();
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
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS livré", Toast.LENGTH_SHORT).show();
                        SmsManager smsKEY = SmsManager.getDefault();
                        smsKEY.sendTextMessage(phone, null, "crypté avec Cesar Mot de passe = 2", null, null);
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

        name = getIntent().getStringExtra("USERNAME");
        phone = getIntent().getStringExtra("PHONE");

        getSupportActionBar().setTitle(name);

        sms = findViewById(R.id.edit_txt_send);

        FloatingActionButton fab = findViewById(R.id.fab_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("rrrr", sms.getText().toString() + " " + phone);
                if (!sms.getText().toString().isEmpty()) {
                    // TODO add requestSmsPermission() dans un try
                    sendSMS(sms.getText().toString());
                    sms.setText("");
                }
            }
        });
    }


    // TODO : prepare la list de sms
    private void prepareSMS() {
        /*smsList.add(new MySms(new MyContact(name, "0678978"), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, phone), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, "0678978"), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, phone), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, phone), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, "0678978"), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));
        smsList.add(new MySms(new MyContact(name, phone), "saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i saluthihh j h b buu jj h i "));*/
    }


    // TODO : faire appel a cette methode
    public void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // Cela signifie que la permission a deja ete demande et l'utilisateur l'a refuse
                // On peut aussi expliquer a l'utilisateur pourquoi
                // cette permission est necessaire et la redemander
            } else {
                // Sinon demander la permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    protected void sendSMS(String msg) {
        try {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
/*
            // Quand le SMS a ete envoye
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS envoye", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));
            // Quand le SMS a ete livre
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS livre", Toast.LENGTH_SHORT).show();
                            SmsManager smsKEY = SmsManager.getDefault();
                            smsKEY.sendTextMessage(phone, null, "crypté avec Cesar Mot de passe = 2", null, null);
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));
*/
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "MY_APP " + Cesar.crypter(2, msg), sentPendingIntent, deliveredPendingIntent);
            // Toast.makeText(getApplicationContext(), "SMS envoye.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), "Envoie faild. Verifier les permissions.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie
                } else {
                    // La permission est refusee
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
