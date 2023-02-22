package com.betathome.android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import com.betathome.helpers.ActionHandler;
import com.betathome.helpers.DataHandler;
import com.betathome.services.DialogUpdateAlertService;
import com.betathome.services.OperatorSpecifics;
import com.betathome.utils.NativeBridge;

import ie.imobile.extremepush.BuildConfig;
import ie.imobile.extremepush.PushConnector;
import static ie.imobile.extremepush.PushConnector.mPushConnector;

public class MainActivity extends AppCompatActivity {

    public static int currentVersionCode = BuildConfig.VERSION_CODE;

    private String mCM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private int photoNr = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        Uri[] results = null;

        //Check if response is positive
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FCR) {

                if (null == mUMA) {
                    return;
                }
                if (intent == null) {
                    //Capture Photo if no image available
                    if (mCM != null) {
                        results = new Uri[]{Uri.parse(mCM)};
                    }
                } else {
                    String dataString = intent.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    } else {
                        Bundle extras = intent.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        photoNr++;
                        File file = new File(getCacheDir(), String.format("camera_photo%d.jpg", photoNr));
                        OutputStream outputStream = null;
                        try {
                            outputStream = new BufferedOutputStream(new FileOutputStream(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                        try {
                            Objects.requireNonNull(outputStream).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        results = new Uri[]{Uri.fromFile(file)};
                    }
                }
            }
        }
        mUMA.onReceiveValue(results);
        mUMA = null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OperatorSpecifics ops = new OperatorSpecifics();

        setContentView(R.layout.activity_main);
        WebView myWebView = findViewById(R.id.webview);
        myWebView.setWebContentsDebuggingEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setDatabaseEnabled(true);
        NativeBridge nativeBridge = new NativeBridge(MainActivity.this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            myWebView.getSettings().setDatabasePath("/data/data/" + myWebView.getContext().getPackageName() + "/databases/");
        }
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (!url.startsWith("https://gamelaunch.everymatrix.com")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        });
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        /** CHECK FOR CAMERA AND FILE UPLOAD PERMISSIONS */
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }


        // Setting new web view when redirecting support links (mail, tel, messenger, live chat, viber)
        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView newWebView = new WebView(MainActivity.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                view.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        view.loadUrl("javascript:" + NativeBridge.getScript());
                    }
                });
                newWebView.getSettings().setSupportMultipleWindows(true);
                newWebView.getSettings().setDomStorageEnabled(true);
                newWebView.getSettings().setDatabaseEnabled(true);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    newWebView.getSettings().setDatabasePath("/data/data/" + newWebView.getContext().getPackageName() + "/databases/");
                }
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                Context context = view.getContext();

                /** FOR WHEN THERE'S NO APP TO OPEN REQUESTED LINK OR TO OPEN WITH AVAILABLE APP */
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        String data = request.getUrl().toString();
                        if (data.contains("@")) {
                            Intent mailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + data));
                            context.startActivity(mailIntent);
                        } else if (data.matches("[0-9 ]+")) {
                            Intent telIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + data));
                            context.startActivity(telIntent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                            try {
                                myWebView.removeView(newWebView);
                                context.startActivity(browserIntent);
                            } catch (ActivityNotFoundException e) {
                                // CREATE AND SHOW ALERT that there's no app to open requested link
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                                HashMap<String, String> errorStrings = ops.getAppErrorStrings();

                                alertBuilder.setTitle(errorStrings.get("title"));
                                alertBuilder.setMessage(errorStrings.get("message"));

                                alertBuilder.setNegativeButton(errorStrings.get("button"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // cancel the dialog
                                    }
                                });

                                AlertDialog alertDialog = alertBuilder.create();
                                alertDialog.show();
                            }
                        }
                        return true;
                    }
                });

                return true;
            }

                // Add onConsoleMessage method
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d("MyApp", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId());
                    return true;
                }

                // Add onJsPrompt method
                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                    Log.d("MyApp", "onJsPrompt " + message);
                    result.confirm();
                    return true;
                }


            /** CREATE CAMERA AND FILE CHOOSER PICKER */
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }

                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                Intent[] intentArray;

                intentArray = new Intent[]{takePictureIntent};

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);

                return true;
            }
        });
        
        /** SETS USER AGENT AND LOADS URL (ALWAYS DELIVER WITH PROD) **/
        if (savedInstanceState == null)
        {
            String releaseVersion = Build.VERSION.RELEASE;
            String currentUA = myWebView.getSettings().getUserAgentString() + "/CustomNative:Android" + "/" + releaseVersion + "/" + currentVersionCode;
            myWebView.getSettings().setUserAgentString(currentUA);
            String url = ops.getSourceUrl();
            myWebView.loadUrl(url);
        }

        /** XtremePush **/
        String xtrmApiKey = ops.getXtrmApiKey();
        String xtrmSessionID = ops.getXtrmApiKeysInstance().getSessionID();
        new PushConnector.Builder(xtrmApiKey, xtrmSessionID).create(this);

        /** CREATE AND SHOW APP UPDATE ALERT DIALOG **/
        DialogUpdateAlertService appUpdateDialogService = new DialogUpdateAlertService();
        appUpdateDialogService.getAppVersion(this, new DataHandler<String>() {
            @Override
            public void onSuccess(String apkToDownloadUrl) {
                // SHOW ALERT
                HashMap<String, String> updateStrings = new HashMap<String, String>();
                OperatorSpecifics ops = new OperatorSpecifics();
                updateStrings = ops.getAppUpdateNotificationStrings();

                showDialogAlert(updateStrings.get("title"),updateStrings.get("message"),updateStrings.get("accept"), updateStrings.get("cancel"), () -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkToDownloadUrl));
                    startActivity(browserIntent);
                });

            }

            @Override
            public void onFailure(Exception exception, String message) {
                // log the exception and show nothing to the user
                Log.d("JSONResponse exception", message);
            }
        });

    }

    public void showDialogAlert(String titleTxt, String msgTxt, String actionButtonTxt, String dismissButtonTxt, ActionHandler alertActionHandler) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(titleTxt);
        alertBuilder.setMessage(msgTxt);

        alertBuilder.setNegativeButton(dismissButtonTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // it cancels the dialog by default
            }
        });


        if (actionButtonTxt != "") {
            alertBuilder.setPositiveButton(actionButtonTxt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // set upon completion the desired action
                    alertActionHandler.action();
                }
            });
        }

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPushConnector.getDeviceInfo(this.getBaseContext());
        String uniqueID = UUID.randomUUID().toString();
        mPushConnector.setUser(uniqueID);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.restoreState(savedInstanceState);
    }

}

