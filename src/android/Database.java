package com.bionworks.contentproviderplugin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Random;

import com.bionworks.contentproviderplugin.SaveUtilData;
import com.bionworks.contentproviderplugin.AndroidKeyStore;

public class Database {
    private static final String DB_NAME = "biondb.db";
    public static final int DB_VERSION = 1;
    public static final String TAG = Database.class.getSimpleName();
    public static final String TABLE_NAME = "Credentials";
    public static final String COLUMN_CREDENTIAL_ID = "credential_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static String CREATE_TABLE_CREDENTIAL = "CREATE TABLE "+TABLE_NAME+"("+COLUMN_CREDENTIAL_ID+" INTEGER PRIMARY KEY, "+COLUMN_COUNTRY_CODE+" INTEGER, "+COLUMN_USERNAME+" TEXT NOT NULL, "+
            COLUMN_PASSWORD+ " TEXT NOT NULL )";
    private SQLiteDatabase sqLliteDatabase;
    private static Database db;
    private static String passPhrase = "";
    private Database(Context context) {
        sqLliteDatabase = new DatabaseHelper(context,DB_NAME,null,DB_VERSION).getWritableDatabase(passPhrase);
    }

    public  static Database getDBInstance(Context context) {
        boolean isSuccess = getOrCreatePassPhrase(context);
        if(!isSuccess) {
            return null;
        }
        SQLiteDatabase.loadLibs(context);
        if (db == null) {
            db = new Database(context);
        }
        return db;
    }

    public static boolean getOrCreatePassPhrase(Context context) {
        try {
            SaveUtilData saveData = new SaveUtilData(context);
            JSONObject obj = saveData.getData();
            AndroidKeyStore keyStore = new AndroidKeyStore();
            if(obj.isNull("key")) {
                passPhrase = getNewRandomPhrase(20);
                Log.i("DataBase","Create Key"+passPhrase);
                boolean isEncrypted = keyStore.encryptData(passPhrase, context);
                if(!isEncrypted) {
                    return false;
                }
            } else {
                Log.i("DataBase","you have the password need to decrypt"+obj);
                String key = obj.getString("key");
                String enIv = obj.getString("iv");
                passPhrase = keyStore.decryptData(key,enIv);
                Log.i("Database","Pass Phrase decrypted->"+passPhrase);
            }
        } catch(Exception e) {
            Log.e("Database","Exception in getOrCreatePassPhrase"+e.getMessage());
            return false;
        }
        return true;
    }
    public boolean insert(JSONObject inputArgs) throws JSONException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME,inputArgs.getString("userName"));
        contentValues.put(COLUMN_PASSWORD,inputArgs.getString("password"));
        contentValues.put(COLUMN_COUNTRY_CODE,inputArgs.getInt("countryCode"));

        return sqLliteDatabase.insert(TABLE_NAME,null,contentValues) > 0;
    }

    public boolean delete(JSONObject inputArgs) throws JSONException {
        String userName = inputArgs.getString("userName");
        String password = inputArgs.getString("password");
        return sqLliteDatabase.delete(TABLE_NAME,COLUMN_USERNAME+"= ? and "+COLUMN_PASSWORD+"=?",new String[]{userName,password}) > 0;
    }

    public JSONArray getAllCredentials() throws JSONException {
        JSONArray credentialList = new JSONArray();
        Cursor cursor = sqLliteDatabase.query(TABLE_NAME,new String[]{COLUMN_USERNAME,COLUMN_PASSWORD, COLUMN_COUNTRY_CODE},null,null,null,null,null,null);
        if( cursor!=null &cursor.getCount()>0 ) {
            while(cursor.moveToNext()) {
                JSONObject credential = new JSONObject();
                credential.put("userName",cursor.getString(0));
                credential.put("password",cursor.getString(1));
                credential.put("countryCode",cursor.getInt(2));
                credentialList.put(credential);
            }
        }
        cursor.close();
        Log.i("DATABASE","data to be returned"+credentialList);
        return credentialList;
    }

    public boolean updateCredential(String userName,String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("password",password);
        return sqLliteDatabase.update(TABLE_NAME,contentValues, COLUMN_USERNAME+" = '"+userName+"'",null) > 0;
    }

    // will be used in content provider
    public Cursor getCursorsForCredentials() {
        Cursor cursor = sqLliteDatabase.query(TABLE_NAME,new String[]{COLUMN_USERNAME,COLUMN_PASSWORD, COLUMN_COUNTRY_CODE},null,null,null,null,null,null);
        return cursor;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int dbVersion){
            super(context,databaseName,factory,dbVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            //TODO when needed
            Log.i(TAG,"Inside onConfigure");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_CREDENTIAL);
            Log.i(TAG,"Inside onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                              int oldVersion, int newVersion) {
            //TODO when needed
            Log.i(TAG,"inside on upgrade");
        }
    }

    public static String getNewRandomPhrase(int length) {
        Random randomize = new Random();
        // create a random string using alphabets and characters
        char[] charactersRange = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        char[] randomData = new char[length];

        for (int i = 0; i < length - 1; i++) {
            int randomIndex = randomize.nextInt(35 + 1);
            randomData[i] = charactersRange[randomIndex];
        }
        return Arrays.toString(randomData).replaceAll("[^a-zA-Z0-9]", "");
    }
}
