package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
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
import boukou.grace.projectm1ifi.db.room_db.Msg2;
import boukou.grace.projectm1ifi.java_files.cesar.Cesar;


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

    Msg2 msg = new Msg2();

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
                    final String uid = messages[0].getStatus()+"";

                    /*
                    getDisplayOriginatingAddress()  : numero de tel
                     */

                    Toast.makeText(context, "UID : " + uid, Toast.LENGTH_LONG).show();

                    //Log.e("UID", ""+uid);
                    String parts[] = messageBody.substring(11).split(" ", 2);
                    Log.e("UID", String.format("id: %s, sms: %s", parts[0], parts[1]));

                    Toast.makeText(context, "Expediteur : " + phoneNumber, Toast.LENGTH_LONG).show();

                    if (messageBody.contains("MY_APP_SMS ")) {
                        //Toast.makeText(context, "Message : " + messageBody, Toast.LENGTH_LONG).show();
                        // TODO Action de sauvegarder dans la DB
                        //addSmsToDatabase(contentResolver, messages[0]);
                        addSmsCodeToDBApp(context, messages[0]);
                    }
                    if (messageBody.contains("MY_APP_KEY ")) {
                        //Toast.makeText(context, "Cle = " + messageBody, Toast.LENGTH_LONG).show();
                        // TODO Action de sauvegarder la cle dans la DB
                        //addSmsToDatabase(contentResolver, messages[0]);
                        addSmsCleToDBApp(context, messages[0]);
                        decode(context);
                    }
                }
            }
        }
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("StaticFieldLeak")
    public void addSmsCodeToDBApp(Context context/*, ContentResolver contentResolver*/, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(11).split(" ", 2);

        msg.nameReceiver = parts[0];
        msg.phoneReceiver = "receiver";
        msg.sms1 = parts[1];    // sms_crypt
        msg.phoneSender = smsMessage.getOriginatingAddress();

        new AsyncTask<Msg2, Void, Void>() {
            @Override
            protected Void doInBackground(Msg2... msgs) {
                for (Msg2 msg1 : msgs) {
                    db.msg2Dao().insertSms_2(msg1);
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

        String parts[] = smsMessage.getMessageBody().substring(11).split(" ", 2);

        msg.phoneReceiver = smsMessage.getOriginatingAddress();
        msg.key = parts[1];    // sms_crypt

        new AsyncTask<Msg2, Void, Void>() {
            @Override
            protected Void doInBackground(Msg2... msgs) {
                for (Msg2 msg1 : msgs) {
                    db.msg2Dao().updateKeySms_22(parts[0], msg1.key);
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


    public void decode(Context context) {
        /*db = AppDatabase.getDatabase(context);
        if (msg.key != null || msg.sms1 != null) {
            msg.sms2 = "COUCOU";//Cesar.decrypter(Integer.parseInt(msg.key), msg.sms1);
        }
        Log.e("SMS", msg.toString());*/
    }

    public void deleteSMSFromSMSDB(Context context, String message, String number) {
        try {
            Log.i("INFO DB SMS", "Suppression du SMS");
            Uri uriSMS = Uri.parse("content://sms/inbox");
            Cursor cursor = context.getContentResolver().query(uriSMS, new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    String body = cursor.getString(5);
                    String creator = cursor.getString(5); // Optionnel
                    if (message.equals(body) && address.equals(number)) {
                        Log.i("INFO DB SMS", "Suppression du SMS: " + threadId);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null
                        );
                    }
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("ERROR DB SMS", "Ne peut pas supprimer le SMS: " + e.getMessage());
        }
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
