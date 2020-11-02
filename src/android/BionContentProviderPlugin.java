package com.bionworks.contentproviderplugin;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bionworks.contentproviderplugin.Database;
import java.util.HashMap;
import java.util.List;


public class BionContentProviderPlugin extends CordovaPlugin {
    private CallbackContext callback;
    private String WRONG_PARAMS = "Wrong parameters.";
    private Database db;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callback = callbackContext;
        Log.i("ContentProvider","logginggg------------------------------->");
        final JSONObject inputArgs = args.getJSONObject(0);
        if (action.equals("insertCredential")) {
            Log.i("ContentProvider","data------------------------------->"+inputArgs);
            if (inputArgs == null) {
                this.callback.error(WRONG_PARAMS);
                return false;
            }
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        insert(inputArgs);
                    } catch (Exception e){
                        Log.e("ContentProvider","exception calling insert method"+e.getMessage());
                    }
                }
            });
        } else if(action.equals("getCredential")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        getCredentials();
                    } catch (Exception e) {
                        Log.e("ContentProvider","exception calling getcredentials method"+e.getMessage());
                    }
                }
            });
        } else if(action.equals("updateCredential")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        updateCredentials(inputArgs);
                    } catch (Exception e) {
                        Log.e("ContentProvider","Exception calling Updatecredentials method"+e.getMessage());
                    }
                }
            });
        } else if(action.equals("deleteCredential")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        deleteCredentials(inputArgs);
                    } catch (Exception e) {
                        Log.e("ContentProvider","Exception calling Delete method"+e.getMessage());
                    }
                }
            });
        }
        return true;
    }

    public void insert(JSONObject args) throws JSONException {
        JSONObject response = new JSONObject();
        if(args.getString("userName").isEmpty() || args.getString("password").isEmpty() || args.getInt("countryCode") < 0 ) {
            response.put("status","ERROR");
            response.put("data",null);
            response.put("message",WRONG_PARAMS);
            this.callback.error(response);
        } else {
            db = db.getDBInstance(cordova.getContext());
            if(db == null) {
                response.put("status","ERROR");
                response.put("data",null);
                response.put("message","Something went wrong");
                this.callback.error(response);
            } else {
                boolean success = db.insert(args);
                if(success) {
                    response.put("status","SUCCESS");
                    response.put("data",null);
                    response.put("message","Data Inserted Successfully");
                    this.callback.success(response);
                } else {
                    response.put("status","ERROR");
                    response.put("data",null);
                    response.put("message","Something went wrong");
                    this.callback.error(response);
                }
            }
        }
    }

    public void deleteCredentials(JSONObject args) throws JSONException {
        JSONObject response = new JSONObject();
        if(args.getString("userName").isEmpty() || args.getString("password").isEmpty()) {
            response.put("status","ERROR");
            response.put("data",null);
            response.put("message",WRONG_PARAMS);
            this.callback.error(response);
        } else {
            db = db.getDBInstance(cordova.getContext());
            if(db == null) {
                response.put("status","ERROR");
                response.put("data",null);
                response.put("message","Something went wrong");
                this.callback.error(response);
            } else {
                boolean success = db.delete(args);
                if(success) {
                    response.put("status","SUCCESS");
                    response.put("data",null);
                    response.put("message","Data deleted Successfully");
                    this.callback.success(response);
                } else {
                    response.put("status","ERROR");
                    response.put("data",null);
                    response.put("message","Something went wrong");
                    this.callback.error(response);
                }
            }
        }
    }

    public void getCredentials() throws JSONException {
        JSONObject response = new JSONObject();
        try {
            db = db.getDBInstance(cordova.getContext());
            if( db == null) {
                response.put("status","ERROR");
                response.put("data",null);
                response.put("message","Something went wrong");
                this.callback.error(response);
            } else {
                JSONArray credentialsList = db.getAllCredentials();
                if(credentialsList!=null && credentialsList.length() > 0) {
                    response.put("status","SUCCESS");
                    response.put("data",credentialsList);
                    response.put("message","Data Retrieved Successfully");
                    Log.i("ContentProvider"," values is ------------->"+credentialsList);
                    this.callback.success(response);
                } else {
                    response.put("status","SUCCESS");
                    response.put("data",null);
                    response.put("message","No Data found");
                    this.callback.success(response);
                    Log.i("ContentProvider","no valuues there -------------------------------------");
                }
            }
        } catch (Exception e) {
            response.put("status","ERROR");
            response.put("data",null);
            response.put("message",e.getMessage());
            this.callback.error(response);
        }
    }

    public void updateCredentials(JSONObject args) throws JSONException {
        JSONObject response = new JSONObject();
        if(args.getString("userName").isEmpty() || args.getString("password").isEmpty()) {
            response.put("status","ERROR");
            response.put("data",null);
            response.put("message",WRONG_PARAMS);
            this.callback.error(response);
        } else {
            db = db.getDBInstance(cordova.getContext());
            if( db == null) {
                response.put("status","ERROR");
                response.put("data",null);
                response.put("message","Something went wrong");
                this.callback.error(response);
            } else {
                String userName = args.getString("userName");
                String password = args.getString("password");
                boolean success = db.updateCredential(userName,password);
                if(success) {
                    response.put("status","SUCCESS");
                    response.put("data",null);
                    response.put("message","Data updated Successfully");
                    this.callback.success(response);
                } else {
                    response.put("status","ERROR");
                    response.put("data",null);
                    response.put("message","Something went wrong");
                    this.callback.error(response);
                }
            }
        }
    }
}
