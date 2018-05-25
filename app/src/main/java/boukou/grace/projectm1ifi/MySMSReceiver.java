package boukou.grace.projectm1ifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;
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

    Msg msg = new Msg();
    Msg2 msg_recu = new Msg2();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if (Objects.equals(intent.getAction(), ACTION_RECEIVE_SMS)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = ((Object[]) bundle.get(SMS_EXTRA_NAME));

                final SmsMessage[] messages = new SmsMessage[Objects.requireNonNull(pdus).length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages[0].getMessageBody().charAt(0) == 'i') {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();
                    final String uid = messages[0].getStatus() + "";

                    //Toast.makeText(context, "Expediteur : " + phoneNumber, Toast.LENGTH_LONG).show();

                    if (messageBody.contains("iSMS ")) {
                        Toast.makeText(context, "Vous avez recu un message crypté", Toast.LENGTH_LONG).show();
                        addSmsCodeToDBApp(context, messages[0]);
                        sendAccuse(phoneNumber, messages[0]);
                    }
                    if (messageBody.contains("iACCUSE ")) {
                        Toast.makeText(context, "Message crypté bien recu", Toast.LENGTH_LONG).show();
                        saveAccuse(context, messages[0]);
                        sendKey(context, phoneNumber, messages[0]);
                    }
                    if (messageBody.contains("iKEY ")) {
                        Toast.makeText(context, "Cle du message crypté bien recu", Toast.LENGTH_LONG).show();
                        addSmsCleToDBApp(context, messages[0]);
                        decode(context, messages[0]);
                    }
                }
            }
        }
    }


    public void sendAccuse(String phone, SmsMessage smsMessage) {
        String parts[] = smsMessage.getMessageBody().substring(5).split(" ", 2);
        try {
            SmsManager accuse = SmsManager.getDefault();
            accuse.sendTextMessage(phone, null, "iACCUSE " + parts[0], null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void saveAccuse(Context context, /*String phone, */SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().split(" ", 2);

        msg.status_sms = "sms recu";

        new AsyncTask<Msg, Void, Void>() {

            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().updateStatusSms(parts[1], msg1.status_sms);
                }
                return null;
            }
        }.execute(msg);
    }

    public void sendKey(Context context, String phone, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(8).split(" ", 2);

        String key = db.msgDao().getKey(parts[0]);

        try {
            SmsManager smsKEY = SmsManager.getDefault();
            smsKEY.sendTextMessage(phone, null, "iKEY " + parts[0] + " " + key, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void addSmsCodeToDBApp(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(5).split(" ", 2);

        msg_recu.nameReceiver = parts[0];
        msg_recu.phoneReceiver = "receiver";
        msg_recu.sms1 = parts[1];    // sms_crypt
        msg_recu.phoneSender = smsMessage.getOriginatingAddress();

        new AsyncTask<Msg2, Void, Void>() {
            @Override
            protected Void doInBackground(Msg2... msgs) {
                for (Msg2 msg1 : msgs) {
                    db.msg2Dao().insertSms_2(msg1);
                }
                return null;
            }
        }.execute(msg_recu);
        Log.e("SMS", msg_recu.toString());
        deleteSMSFromSMSDB(context, smsMessage.getMessageBody(), smsMessage.getOriginatingAddress());
        Log.e("GRACE", String.format("message supprime"));
    }


    @SuppressLint("StaticFieldLeak")
    public void addSmsCleToDBApp(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(5).split(" ", 2);

        msg_recu.phoneReceiver = smsMessage.getOriginatingAddress();
        msg_recu.key = parts[1];    // sms_crypt

        new AsyncTask<Msg2, Void, Void>() {
            @Override
            protected Void doInBackground(Msg2... msgs) {
                for (Msg2 msg1 : msgs) {
                    db.msg2Dao().updateKeySms_22(parts[0], msg1.key);
                }
                return null;
            }
        }.execute(msg_recu);
        Log.e("SMS", msg_recu.toString());
        deleteSMSFromSMSDB(context, smsMessage.getMessageBody(), smsMessage.getOriginatingAddress());
        Log.e("GRACE", String.format("message supprime"));
    }


    public void decode(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);
        String parts[] = smsMessage.getMessageBody().substring(8).split(" ", 2);

        String sms = db.msg2Dao().getKey_2(parts[0]);
        String key = db.msg2Dao().getSms_2(parts[0]);

        Log.e("TTTTTT", sms+ " : " + key + " : "+Cesar.decrypter(Integer.parseInt(msg_recu.key), "Coucou"));

        if (sms != null || key != null) {
         //   msg_recu.sms2 = "COUCOU";//Cesar.decrypter(Integer.parseInt(msg_recu.key), msg_recu.sms1);
            //SmsManager smsKEY = SmsManager.getDefault();
            //smsKEY.sendTextMessage(, null, "COUCOU " + Cesar.decrypter(Integer.parseInt(msg_recu.key), msg_recu.sms1), null, null);

            Log.e("EEEE", sms+ " : " + key + " : "+Cesar.decrypter(Integer.parseInt(msg_recu.key), "Coucou"));

            //insertSMSInSMSDB(context, "GRACE___"/* + Cesar.decrypter(Integer.parseInt(msg_recu.key), msg_recu.sms1)*/, msg_recu.phoneReceiver);

            Log.e("SMS", msg_recu.toString());
        }
    }

    public void insertSMSInSMSDB(Context context, String message, String number) {
        try {
            Uri uriSMS = Uri.parse("content://sms/");
            ContentValues contentValues = new ContentValues();
            contentValues.put("address", number);
            contentValues.put("body", message);
            contentValues.put("type", 1);   // 1 pour MESSAGE_TYPE_INBOX

            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), contentValues);
        } catch (Exception e) {
            Log.e("ERROR", "Ne peut pas supprimer le SMS: " + e.getMessage());
        }
    }

    public void deleteSMSFromSMSDB(Context context, String message, String number) {
        try {
            //Log.i("INFO", "Suppression du SMS");
            Uri uriSMS = Uri.parse("content://sms");
            Cursor cursor = context.getContentResolver().query(uriSMS, new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                //do {
                    long id = cursor.getLong(0);
                    long threadId = cursor.getLong(1);
                    String address = cursor.getString(2);
                    String body = cursor.getString(5);

                /*Cursor cursor2 = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                        null, null, null);*/

                Log.i("INFO", ""+ id + " " + threadId + " " + address + " " + body + " ");

                    /*if (message.equals(body) && message.contains("MY_APP_SMS ")) {
                        Log.i("INFO", ""+ id + " " + threadId + " " + address + " " + body + " ");
                        context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId), null, null);
                        Log.i("INFO", "Suppression du SMS: " + id);
                    }*/
                context.getContentResolver().delete(Uri.parse("content://sms"), "thread_id=? and _id=?", new String[]{String.valueOf(threadId), String.valueOf(id)});

                    String creator = cursor.getString(5); // Optionnel
                    /*if (message.equals(body) && address.equals(number)) {
                        Log.i("INFO", "Suppression du SMS: " + threadId);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null
                        );
                    }*/
                //} while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("ERROR", "Ne peut pas supprimer le SMS: " + e.getMessage());
        }
    }
}
