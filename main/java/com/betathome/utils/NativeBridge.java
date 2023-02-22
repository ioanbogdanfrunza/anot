package com.betathome.utils;

import android.content.Context;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class NativeBridge {
    private WebView webView;
    private Map<String, EventListener> eventListeners;
    private PostMessageHandler postMessageHandler;

    public NativeBridge(WebView webView) {
        this.webView = webView;
        eventListeners = new HashMap<>();
        postMessageHandler = new PostMessageHandler();
        init();
    }

    private void init() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(webView.getContext()), "android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:" + getScript());
            }
        });
    }

    public static String getScript() {
        String script = "var cstmNativeBridge = cstmNativeBridge || {}; " +
                "cstmNativeBridge.eventListeners = cstmNativeBridge.eventListeners || {}; " +
                "cstmNativeBridge.EVENTS = {";

        for (NativeBridgeEvents event : NativeBridgeEvents.values()) {
            script += "'" + event.name() + "': '" + event.name() + "',";
        }

        script += "}; cstmNativeBridge.METHODS = {";

        for (NativeBridgeMethods method : NativeBridgeMethods.values()) {
            script += "'" + method.name() + "': '" + method.name() + "',";
        }

        script += "}; cstmNativeBridge.broadcastEvent = function (event) { " +
                "if (cstmNativeBridge.eventListeners[event]) { " +
                "var newArgs = Array.prototype.slice.call(arguments, 1); " +
                "cstmNativeBridge.eventListeners[event].apply(this, newArgs); " +
                "} }; cstmNativeBridge.sendMessage = function (message) { " +
                "window.android.postMessage(JSON.stringify(message)); }; " +
                "Object.keys(cstmNativeBridge.METHODS).forEach(function(methodKey) { " +
                "var methodStr = cstmNativeBridge.METHODS[methodKey]; " +
                "cstmNativeBridge[methodStr] = function (data) { " +
                "cstmNativeBridge.sendMessage({ type: methodStr, data: data }); }; });";

        return script;
    }

    public enum NativeBridgeEvents {
        OPEN_LOGIN,
        OPEN_REGISTER,
        ENABLE_BIOMETRICS,
        DISABLE_BIOMETRICS,
        BIOMETRICS_ENABLED,
        GET_CREDENTIALS,
        UPDATE_CREDENTIALS,
        CLEAR_CREDENTIALS,
        OPEN_GAME,
        OPEN_URL
    }

    public enum NativeBridgeMethods {
        ENABLE_BIOMETRICS,
        DISABLE_BIOMETRICS,
        BIOMETRICS_ENABLED,
        GET_CREDENTIALS,
        UPDATE_CREDENTIALS,
        CLEAR_CREDENTIALS,
        OPEN_GAME,
        OPEN_URL
    }

    public void addEventListener(String event, EventListener listener) {
        eventListeners.put(event, listener);
    }

    public void removeEventListener(String event) {
        eventListeners.remove(event);
    }

    public void removeAllEventListeners() {
        eventListeners.clear();
    }

    public interface EventListener {
        void onEvent(String event, JSONObject data);
    }

    private class WebAppInterface {
        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void postMessage(String message) {
            Object objectMessage = message;
             postMessageHandler.handleMessage((Message) objectMessage);
        }
    }

    private class PostMessageHandler extends Handler {
        public void handleMessage(Message message) {
            if (message != null && message.obj != null) {
                String jsonMessage = (String) message.obj;
                try {
                    JSONObject jsonObject = new JSONObject(jsonMessage);
                    String type = jsonObject.optString("type");
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (type != null) {
                        switch (type) {
                            case "OPEN_LOGIN":
                                handleOpenLogin(data);
                                break;
                            case "OPEN_REGISTER":
                                handleOpenRegister(data);
                                break;
                            case "ENABLE_BIOMETRICS":
                                handleEnableBiometrics();
                                break;
                            case "DISABLE_BIOMETRICS":
                                handleDisableBiometrics();
                                break;
                            case "BIOMETRICS_ENABLED":
                                handleBiometricsEnabled();
                                break;
                            case "GET_CREDENTIALS":
                                handleGetCredentials();
                                break;
                            case "UPDATE_CREDENTIALS":
                                handleUpdateCredentials(data);
                                break;
                            case "CLEAR_CREDENTIALS":
                                handleClearCredentials();
                                break;
                            case "OPEN_GAME":
                                handleOpenGame(data);
                                break;
                            case "OPEN_URL":
                                handleOpenUrl(data);
                                break;
                            default:
                                // Unknown event type
                                break;
                        }
                    }
                } catch (JSONException e) {
                    // Error parsing JSON message
                }
            }
        }

        private void handleOpenUrl(JSONObject data) {}

        private void handleOpenGame(JSONObject data) {}

        private void handleClearCredentials() {}

        private void handleUpdateCredentials(JSONObject data) {}

        private void handleGetCredentials() {}

        private void handleBiometricsEnabled() {}

        private void handleDisableBiometrics() {}

        private void handleEnableBiometrics() {}

        private void handleOpenRegister(JSONObject data) {}

        private void handleOpenLogin(JSONObject data) {}

        @Override
        public void publish(LogRecord logRecord) {}

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}