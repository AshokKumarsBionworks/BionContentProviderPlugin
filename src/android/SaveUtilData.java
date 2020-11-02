package com.bionworks.contentproviderplugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SaveUtilData {
    private SharedPreferences sharedPreferences;
    private final String key="Key";
    private final String enIv="IV";

    public SaveUtilData(Context context) {
        Log.i("SaveUtilData","packagename check->"+context.getPackageName());
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setData(String pass, String iv) {
        Log.i("SaveUtilData","data ->"+pass+" "+iv+" "+sharedPreferences);
        sharedPreferences.edit().putString(key, pass).apply();
        sharedPreferences.edit().putString(enIv, iv).apply();
    }

    public JSONObject getData() throws JSONException {
        JSONObject response = new JSONObject();
        String pass = sharedPreferences.getString(key,null);
        String iv = sharedPreferences.getString(enIv,null);
        response.put("key",pass);
        response.put("iv",iv);
        return response;
    }
}
