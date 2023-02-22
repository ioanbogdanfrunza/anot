package com.betathome.services;

import com.betathome.android.BuildConfig;
import com.betathome.strings.OperatorStrings;
import com.betathome.strings.SourceURLs;
import com.betathome.strings.XtremePushAPIKeys;

import java.util.HashMap;

public class OperatorSpecifics {
    private final String appId = BuildConfig.APPLICATION_ID;
    private final String buildType = BuildConfig.BUILD_TYPE;
    private String domain = "", lang = "";

    /** do it with maps */
    public HashMap<String, String> getAppErrorStrings() {
        HashMap<String, String> appErrorStrings = new HashMap<String, String>();

        switch (appId) {
            case "com.bah.sportsapp.release":
                appErrorStrings.put("title", OperatorStrings.getComAppErrorTitle());
                appErrorStrings.put("message", OperatorStrings.getComAppErrorMessage());
                appErrorStrings.put("button", OperatorStrings.getComAppErrorBttn());
                break;
            case "com.bah.casinoapp":
                appErrorStrings.put("title", OperatorStrings.getDeAppErrorTitle());
                appErrorStrings.put("message", OperatorStrings.getDeAppErrorMessage());
                appErrorStrings.put("button", OperatorStrings.getDeAppErrorBttn());
                break;

        }
        return appErrorStrings;
    }

    public HashMap<String, String> getAppUpdateNotificationStrings() {
        HashMap<String, String> appUpdateStrings = new HashMap<String, String>();
        switch (appId) {
            case "com.bah.sportsapp.release":
                appUpdateStrings.put("title", OperatorStrings.getComAppUpdateTitle());
                appUpdateStrings.put("message", OperatorStrings.getComAppUpdateMessage());
                appUpdateStrings.put("accept", OperatorStrings.getComAppUpdateAcceptBttn());
                appUpdateStrings.put("cancel", OperatorStrings.getComAppUpdateCancelBttn());
                break;
            case "com.bah.casinoapp":
                appUpdateStrings.put("title", OperatorStrings.getDeAppUpdateTitle());
                appUpdateStrings.put("message", OperatorStrings.getDeAppUpdateMessage());
                appUpdateStrings.put("accept", OperatorStrings.getDeAppUpdateAcceptBttn());
                appUpdateStrings.put("cancel", OperatorStrings.getDeAppUpdateCancelBttn());
                break;
        }

        return appUpdateStrings;
    }

    public String getApiJSONUrl() {
        initialiseApiUrlParams();

        return String.format("https://www.bet-at-home.com/.%s/apijson/%s/extra-resources?env=prod", domain, lang);
    }

    public String getXtrmApiKey() {
        switch (appId) {
            case "com.bah.sportsapp.release":
                return XtremePushAPIKeys.getComXtrmApiKey();
            case "com.bah.casinoapp":
                return XtremePushAPIKeys.getDeXtrmApiKey();
        }
        return "";
    }

    public String getSourceUrl() {
        switch (buildType) {
            case "staging":
                switch (appId) {
                    case "com.bah.sportsapp.release":
                        return SourceURLs.getComStageURL();
                    case "com.bah.casinoapp":
                        return SourceURLs.getDeStageURL();
                }

            case "release":
                switch (appId) {
                    case "com.bah.sportsapp.release":
                        return SourceURLs.getComProdURL();
                    case "com.bah.casinoapp":
                        return SourceURLs.getDeProdURL();
                }

            case "debug":
                switch (appId) {
                    case "com.bah.sportsapp.release":
                        return SourceURLs.getComStageURL();
                    case "com.bah.casinoapp":
                        return SourceURLs.getDeStageURL();
                }

        }

        return SourceURLs.getComProdURL();
    }

    public XtremePushAPIKeys getXtrmApiKeysInstance() {
        return new XtremePushAPIKeys();
    }

    public SourceURLs getSourceUrlInstance() {
        return new SourceURLs();
    }

    private void initialiseApiUrlParams() {
        switch (appId) {
            case "com.bah.sportsapp.release":
                domain = "com";
                lang = "en";
                return;
            case "com.bah.casinoapp":
                domain = "de";
                lang = "de";
                return;
        }
    }
}



