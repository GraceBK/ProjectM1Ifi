package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;
import boukou.grace.projectm1ifi.db.room_db.RContact;


public class MySMSReceiver extends BroadcastReceiver {

    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";

    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    private AppDatabase db;

    Msg msg = new Msg();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if (Objects.equals(intent.getAction(), ACTION_RECEIVE_SMS)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = ((Object[]) bundle.get(SMS_EXTRA_NAME));

                ContentResolver contentResolver = context.getContentResolver();

                final SmsMessage[] messages = new SmsMessage[Objects.requireNonNull(pdus).length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context, "Expediteur : " + phoneNumber, Toast.LENGTH_LONG).show();

                    if (messageBody.contains("MY_APP_SMS ")) {
                        Toast.makeText(context, "Message : " + messageBody, Toast.LENGTH_LONG).show();
                        // TODO Action de sauvegarder dans la DB
                        //addSmsToDatabase(contentResolver, messages[0]);
                        addSmsCodeToDBApp(context, messages[0]);
                    }
                    if (messageBody.contains("MY_APP_KEY ")) {
                        Toast.makeText(context, "Cle = " + messageBody, Toast.LENGTH_LONG).show();
                        // TODO Action de sauvegarder la cle dans la DB
                        //addSmsToDatabase(contentResolver, messages[0]);
                        addSmsCleToDBApp(context, messages[0]);
                    }
                }
            }
        }
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*@ColumnInfo(name = "name_receiver")
    public String nameReceiver;


    @ColumnInfo(name = "numero_sender")
    public String phoneSender;

    @ColumnInfo(name = "sms_decrypt")
    public String sms2;

    @ColumnInfo(name = "cle")
    public String key;*/

    @SuppressLint("StaticFieldLeak")
    public void addSmsCodeToDBApp(Context context/*, ContentResolver contentResolver*/, SmsMessage smsMessage) {

        db = AppDatabase.getDatabase(context);
//        db.msgDao().insertSms();

        msg.phoneReceiver = "receiver";
        msg.sms1 = smsMessage.getMessageBody().substring(11);    // sms_crypt
        msg.phoneSender = smsMessage.getOriginatingAddress();

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().insertSms(msg1);
                }
                return null;
            }
        }.execute(msg);
        Log.e("SMS", msg.toString());

        /*ContentValues values = new ContentValues();
        values.put(DATE, smsMessage.getTimestampMillis());
        values.put(READ, MESSAGE_IS_NOT_READ);
        values.put(STATUS, smsMessage.getStatus());
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(SEEN, MESSAGE_IS_NOT_SEEN);

        contentResolver.insert(Uri.parse(SMS_URI), values);*/
    }


    @SuppressLint("StaticFieldLeak")
    public void addSmsCleToDBApp(Context context, SmsMessage smsMessage) {

        db = AppDatabase.getDatabase(context);
//        db.msgDao().insertSms();

        /*msg.phoneReceiver = smsMessage.getOriginatingAddress();
        msg.key = smsMessage.getMessageBody().substring(11);    // sms_crypt

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().updateKeySms(msg1.nameReceiver, msg1.key);
                }
                return null;
            }
        }.execute(msg);
        Log.e("SMS", msg.toString());*/

        /*ContentValues values = new ContentValues();
        values.put(DATE, smsMessage.getTimestampMillis());
        values.put(READ, MESSAGE_IS_NOT_READ);
        values.put(STATUS, smsMessage.getStatus());
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(SEEN, MESSAGE_IS_NOT_SEEN);

        contentResolver.insert(Uri.parse(SMS_URI), values);*/
    }



    public void addSmsDecodeToSMS(ContentResolver contentResolver, SmsMessage smsMessage) {
        ContentValues values = new ContentValues();
        values.put(ADDRESS, smsMessage.getOriginatingAddress());
        values.put(DATE, smsMessage.getTimestampMillis());
        values.put(READ, MESSAGE_IS_NOT_READ);
        values.put(STATUS, smsMessage.getStatus());
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(SEEN, MESSAGE_IS_NOT_SEEN);

        values.put(BODY, smsMessage.getMessageBody().substring(7)+"@");

        contentResolver.insert(Uri.parse(SMS_URI), values);
    }

    public void addSmsToDatabase(ContentResolver contentResolver, SmsMessage smsMessage) {
        ContentValues values = new ContentValues();
        values.put(ADDRESS, smsMessage.getOriginatingAddress());
        values.put(DATE, smsMessage.getTimestampMillis());
        values.put(READ, MESSAGE_IS_NOT_READ);
        values.put(STATUS, smsMessage.getStatus());
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(SEEN, MESSAGE_IS_NOT_SEEN);

        values.put(BODY, smsMessage.getMessageBody().substring(7)+"@");

        contentResolver.insert(Uri.parse(SMS_URI), values);
    }

    public void lireSms(Context context) {
        // La table de string qui contient tous les sms
        List<String> sms_list = getAllSmsFromProvider(context);

        if (!sms_list.isEmpty()) {
            for (int i = 0; i < sms_list.size(); i++) {
                // Expression regex
                String mon_sms = sms_list.get(i);
                if (mon_sms != null) {
                    if (mon_sms.contains("MY_APP ")) {
                        String sms = sms_list.get(i).substring(7);
                        Log.e("SMS = ", sms);
                    }
                }
            }
        }
    }

    public List<String> getAllSmsFromProvider(Context context) {
        List<String> listSMS = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[]{Telephony.Sms.Inbox.BODY},
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        int totalSms = Objects.requireNonNull(cursor).getCount();
        int cpt = 0;
        if (cursor.moveToFirst()) {
            for (int i = 0; i < totalSms; i++) {
                listSMS.add(cursor.getString(0));
                cursor.moveToNext();
                cpt++;
            }
        } else {
            Log.e("ERREUR","You have no SMS in Inbox");
        }
        cursor.close();

        //TelephonyProvider telephonyProvider = new TelephonyProvider(context);

        return listSMS;
    }
}
