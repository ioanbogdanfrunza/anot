package com.betathome.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.betathome.android.BuildConfig;
import com.betathome.helpers.DataHandler;

public class DialogUpdateAlertService {
    private int currentVersionCode = BuildConfig.VERSION_CODE;
    private String currentVersionName = BuildConfig.VERSION_NAME;

    String apkDownloadableUrl = "";


    public void getAppVersion(Context context, DataHandler<String> dataHandler) {
        OperatorSpecifics operatorSpecifics = new OperatorSpecifics();
//        String url = "https://www.winmasters.gr/apijson/el/extra-resources?env=prod"; /** ONLY FOR TESTING*/
        String url = operatorSpecifics.getApiJSONUrl();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        String typeOfScript = jsonObject.getString("type");

                        if (typeOfScript.contentEquals("apk_link") ) {
                            String apkUrl = jsonObject.getString("content");
                            apkDownloadableUrl = apkUrl;
                            String versionNr = parseString(apkUrl);
                            if (!versionNr.isEmpty()) {
//                                dataHandler.onSuccess(versionNr);
                                if (checkVersionCode(versionNr)) {
                                    dataHandler.onSuccess(apkUrl);
                                } else {
                                    dataHandler.onFailure(null, "Failed upon version nr check");
                                }
                            } else {
                                dataHandler.onFailure(null, "No version number found after parsing.");
                            }

                            break;

                        } else if (i == response.length() - 1) {
                            dataHandler.onFailure(null, "The JSON response doesn't contain the apk url. Contact The Crow.");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    dataHandler.onFailure(e,"JSON exception (e.g.parsing)");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dataHandler.onFailure(error,"Error on listening to response");
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private String parseString(String apkUrl) {
        // In case we need url parsing validation or a build to mock push update notification
//        apkUrl = "https://storage.googleapis.com/stateless-winmasters-one/2021/08/aec423-winmasters-apk-v16.apk"; // always comment !

        String appVers = "";
        appVers = apkUrl.substring(apkUrl.length() - 6, apkUrl.length() - 4);

        return appVers;
    }

    private boolean checkVersionCode(String appVersionCode)  {
        // get local version; compare to the online version; show alert
        int versionCode = 0;
        try {
            versionCode = Integer.parseInt(appVersionCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("parsingVersionCode", "The versionCode string couldn't be parsed.");
            return false;
        }

        if (currentVersionCode == versionCode) {
            Log.d("appVersion","This is already the last app version: " + appVersionCode);
        } else if (currentVersionCode < versionCode){
            // SHOW ALERT
            return true;
        } else if (currentVersionCode > versionCode ) {
            // Don't forget to update in CMS
            Log.d("ALERT: appVersion",String.format("The currentVersionCode: %d shouldn't be greater than CMS apk versionCode: %d. Don't forget to update in CMS. ", currentVersionCode, versionCode));
            return false;
        }

        return false;
    }
}
