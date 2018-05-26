package boukou.grace.projectm1ifi;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Objects;

import boukou.grace.projectm1ifi.db.room_db.AppDatabase;
import boukou.grace.projectm1ifi.db.room_db.Msg;
import boukou.grace.projectm1ifi.java_files.cesar.Cesar;


public class MySMSReceiver extends BroadcastReceiver {

    public static final String SMS_EXTRA_NAME = "pdus";

    private AppDatabase db;

    Msg msg = new Msg();

    @Override
    public void onReceive(Context context, Intent intent) {
        String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
        if (Objects.equals(intent.getAction(), ACTION_RECEIVE_SMS)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] pdus = ((Object[]) bundle.get(SMS_EXTRA_NAME));

                final SmsMessage[] messages = new SmsMessage[Objects.requireNonNull(pdus).length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                //if (messages[0].getMessageBody().charAt(0) == 'i') {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();

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
                    if (messageBody.contains("iLU ")) {
                        smsLu(context, messages[0]);
                    }
                //}
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
    public void saveAccuse(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().split(" ", 2);

        msg.status_sms = "Message recu";

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

        msg.nameReceiver = parts[0] + "r";
        msg.phoneReceiver = smsMessage.getOriginatingAddress();
        msg.sms1 = parts[1];    // sms_crypt
        msg.phoneSender = "receiver";

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().insertSms(msg1);
                }
                return null;
            }
        }.execute(msg);
    }

    @SuppressLint("StaticFieldLeak")
    public void addSmsCleToDBApp(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(5).split(" ", 2);

        msg.phoneReceiver = smsMessage.getOriginatingAddress();
        msg.key = parts[1];    // sms_crypt

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().updateKeySms(parts[0] + "r", msg1.key);
                }
                return null;
            }
        }.execute(msg);
    }

    @SuppressLint("StaticFieldLeak")
    public void decode(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);
        String parts[] = smsMessage.getMessageBody().substring(5).split(" ", 2);

        String sms = db.msgDao().getSms(parts[0] + "r");
        String key = parts[1];

        //Log.e("-------", "cle = "+key+ " SMS = "+sms + " part "+ parts[0]);

        msg.sms2 = Cesar.decrypter(Integer.parseInt(key), sms);

        new AsyncTask<Msg, Void, Void>() {
            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().updateSmsDecrypt(parts[0] + "r", msg1.sms2);
                }
                return null;
            }
        }.execute(msg);

        createNotification(context, "Nouveau SMS dechiffre", Cesar.decrypter(Integer.parseInt(key), sms));
    }

    public void createNotification(Context context, String msgAlert, String msgText) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "grace_bk_id")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Nouveau SMS dechiffre")
                .setTicker(msgAlert)
                .setContentText(msgText);
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(manager).notify(1, builder.build());
    }

    @SuppressLint("StaticFieldLeak")
    public void smsLu(Context context, SmsMessage smsMessage) {
        db = AppDatabase.getDatabase(context);

        String parts[] = smsMessage.getMessageBody().substring(4).split(" ", 2);

        msg.status = "Message Lu";

        new AsyncTask<Msg, Void, Void>() {

            @Override
            protected Void doInBackground(Msg... msgs) {
                for (Msg msg1 : msgs) {
                    db.msgDao().updateStatus(parts[0].substring(0, parts[0].length()-1), msg1.status);
                }
                return null;
            }
        }.execute(msg);
    }

}
