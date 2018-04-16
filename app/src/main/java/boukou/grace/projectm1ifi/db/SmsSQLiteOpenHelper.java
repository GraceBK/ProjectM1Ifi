package boukou.grace.projectm1ifi.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import boukou.grace.projectm1ifi.java_files.MySms;

/**
 * boukou.grace.projectm1ifi.db
 * Created by grace on 15/04/2018.
 */
public class SmsSQLiteOpenHelper extends SQLiteOpenHelper {

    // DATABASE VERSION AND NAME
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sms_db";

    // TABLE AND COLUMNS NAMES
    private static final String TABLE_SMS = "my_sms";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CONTACT_ID = "contact_id";
//    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DATE_SENT = "date_sent";
    private static final String COLUMN_BODY_ENCRYPTED = "body_encrypted";
    private static final String COLUMN_BODY_DECRYPTED = "body_decrypted";
    private static final String COLUMN_KEY = "_key";
    private static final String COLUMN_SENDER = "sender";

    private static final String[] COLOMNS = {COLUMN_ID, COLUMN_CONTACT_ID,
            /*COLUMN_DATE,*/ COLUMN_DATE_SENT, COLUMN_BODY_ENCRYPTED, COLUMN_BODY_DECRYPTED, COLUMN_KEY, COLUMN_SENDER};

    public SmsSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO : Instruction SQL pour creer une table SMS
        String CREATE_SMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SMS + " (" +
                "" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                "" + COLUMN_CONTACT_ID + " TEXT," +
                "" + COLUMN_DATE_SENT + " TEXT," +
                "" + COLUMN_BODY_ENCRYPTED + " TEXT," +
                "" + COLUMN_BODY_DECRYPTED + " TEXT," +
                "" + COLUMN_KEY + " TEXT," +
                "" + COLUMN_SENDER + " TEXT)";
        // Creation de la table SMS
        db.execSQL(CREATE_SMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO : supprimer l'ancienne table si elle existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        // creer une nouvelle table SMS
        this.onCreate(db);
    }

    // TODO pointeur vers le sms
    private MySms cursorToSms(Cursor cursor) {
        MySms mySms = new MySms();
        mySms.set_id(cursor.getInt(0));// ID
        mySms.set_address(cursor.getString(1));// CONTACT_ID
        mySms.set_time(cursor.getString(2));// DATE_SENT
        mySms.set_msg(cursor.getString(3));// BODY_ENCRYPTED message crypte
        mySms.set_key(cursor.getString(5));// KEY
        mySms.set_sender(cursor.getString(6));// SENDER
        return mySms;
    }

    /*
    Les requetes
     */

    public void addSMS(MySms sms) {
        Log.d("ADD_SMS", sms.toString());
        // 1. obtenir une reference a l'ecriture de la DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. creation d'un ContentValues avec ajout de la cle "column"/value
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ID, sms.get_address());
//        values.put(COLUMN_DATE, "");
        values.put(COLUMN_DATE_SENT, sms.get_time());
        values.put(COLUMN_BODY_ENCRYPTED, sms.get_msg());
        values.put(COLUMN_BODY_DECRYPTED, "");
        values.put(COLUMN_KEY, sms.get_key());
        values.put(COLUMN_SENDER, sms.get_sender());
        // 3. insert
        db.insert(TABLE_SMS, null, values);    // key/value -> keys = noms de column/ values = nom des values
        // 4. close
        db.close();
    }

    public void deleteSMS(MySms sms) {
        // 1. obtenir une reference a l'ecriture de la DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. Suppression
        db.delete(TABLE_SMS, COLUMN_ID+" = ?", new String[]{String.valueOf(sms.get_id())});
        // 3. close
        db.close();
        Log.d("DELETE_SMS", sms.toString());
    }

    public void updateSMS(MySms sms) {
        // 1. obtenir une reference a l'ecriture de la DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. creation d'un ContentValues avec ajout de la cle "column"/value
        ContentValues values = new ContentValues();
//        values.put(COLUMN_DATE, "");
        values.put(COLUMN_DATE_SENT, sms.get_time());
        values.put(COLUMN_BODY_ENCRYPTED, sms.get_msg());
        values.put(COLUMN_BODY_DECRYPTED, "");
        // 3. mise a jour ligne
        db.update(TABLE_SMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(sms.get_id())});    // key/value -> keys = noms de column/ values = nom des values
        // 4. close
        db.close();
        Log.d("UPDATE_SMS", sms.toString());
    }

    public MySms getSmsById(int id) {
        // 1. obtenir une reference a l'ecriture de la DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. requete
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_SMS, // a. table
                COLOMNS,    // b. noms des colonnes
                COLUMN_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)},   // d. selections args
                null,   // e. groups by
                null,   // f. having
                null,   // g. order by
                null);  // h. limit
        assert cursor != null;
        return cursorToSms(cursor);
    }

    public List<MySms> getSmsByPhoneNumber(String phone_sender) {
        // 1. obtenir une reference a l'ecriture de la DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. requete
        // SELECT * FROM SMS WHERE ID = id;
        Cursor cursor = db.query(TABLE_SMS, COLOMNS, COLUMN_CONTACT_ID + " LIKE \"" + phone_sender + "\"",
                null, null, null, null);
        // 3.
        List<MySms> smsList = new LinkedList<>();

        MySms sms;
        if (cursor.moveToFirst()) {
            do {
                sms = cursorToSms(cursor);
                // ajout des SMS
                smsList.add(sms);
            } while (cursor.moveToNext());
        }
        Log.e("SMS par TEL ", ""+smsList.size());
        for (int i = 0; i < smsList.size(); i++) {
            Log.e("sms par phone ", ""+smsList.get(i).toString());
        }
        // 4. retourne un SMS
        return smsList;
    }

    public List<MySms> getAllSMS() {
        List<MySms> mySms = new LinkedList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SMS, COLOMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MySms sms = cursorToSms(cursor);
            mySms.add(sms);
            cursor.moveToNext();
        }
        cursor.close();

        Log.e("ALL SMS", ""+mySms.size());
        for (int i = 0; i < mySms.size(); i++) {
            Log.e("sms ", ""+mySms.get(i).toString());
        }
        return mySms;
    }


}
