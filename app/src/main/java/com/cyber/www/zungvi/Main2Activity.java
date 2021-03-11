package com.cyber.www.zungvi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hotchemi.android.rate.AppRate;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;
    private ProgressBar mProgressBar;
    public ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 5173;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    public static final String TAG1 = "Main2Activity";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton floatingActionButton1,floatingActionButton2,floatingActionButton3,floatingActionButton4;
    private boolean greet = false,mic = true,is_a_question = false,need_answer = false;
    String answer = "",request = "";
    String loading_message = "Proceding to your request...please wait", message_de_chargement = "Je procède à votre requête...veuillez patienter";
    String user_id = "";
    Dialog user_dialog;
    String historyUrl = "";


    TextToSpeech textToSpeech;

    @SuppressLint({"SetJavaScriptEnabled","WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("Zungvi");

        //Zungvi Assistant
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                int lang = 0;
                if(status == TextToSpeech.SUCCESS){
                    //Verification de la langue par default de l'appareil pour initilaiser la langue de l'assistante
                    if(Locale.getDefault().getLanguage() == "fr") {
                       lang = textToSpeech.setLanguage(Locale.FRENCH);
                    }else{
                        lang = textToSpeech.setLanguage(Locale.ENGLISH);
                    }

                   if(lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED){
                       Toast.makeText(Main2Activity.this,"Something went wrong with the AI, Language not supported... ",Toast.LENGTH_SHORT).show();
                   }

                }else{
                    Toast.makeText(Main2Activity.this,"Something went wrong with the AI, Voice initialization failed... ",Toast.LENGTH_SHORT).show();
                }

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if(mic == true) {

                            //Mic initialization
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


                            if(Locale.getDefault().getLanguage() == "fr") {
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRANCE.toString());
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Que puis-je faire ?");
                            }else{
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
                            }

                            //Start intent
                            try {
                                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            //End initialization

                        }else{
                           // mic = true;
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });

        //Zungvi Assistant

        if(!DataStore.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }


        if(!isConnected(Main2Activity.this))buildDialog(Main2Activity.this).show();
        else{

            webView = findViewById(R.id.webview);
            mProgressBar = findViewById(R.id.progressBar);
            refreshLayout = findViewById(R.id.refreshLayout);
            floatingActionsMenu = findViewById(R.id.floating_menu);
            floatingActionButton1 = findViewById(R.id.ia_button);
            floatingActionButton2 = findViewById(R.id.add_post_button);
            floatingActionButton3 = findViewById(R.id.tv_button);
            floatingActionButton4 = findViewById(R.id.news_button);
            user_dialog = new Dialog(this);
            mProgressBar.setMax(100);


            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setSupportZoom(false);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            if(Locale.getDefault().getLanguage() == "fr")
            {
                webView.loadUrl("https://zungvi.com/android/fr/fil.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "&version=pegasus");
            }
            else
            {
                webView.loadUrl("https://zungvi.com/android/fil.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "&version=pegasus");
            }
            user_id =  DataStore.getInstance(this).getCurrentUserId()+"";

            floatingActionButton1.setOnClickListener(this);
            floatingActionButton2.setOnClickListener(this);
            floatingActionButton3.setOnClickListener(this);
            floatingActionButton4.setOnClickListener(this);


            // since API 18 cache quota is managed automatically
            if (Build.VERSION.SDK_INT < 18) {
                //noinspection deprecation
                webSettings.setAppCacheMaxSize(9 * 1024 * 1024);  // 5 MB
            }

            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                    myRequest.allowScanningByMediaScanner();
                    myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    DownloadManager myManager =(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    myManager.enqueue(myRequest);

                    Toast.makeText(Main2Activity.this,"Your file is downloading...",Toast.LENGTH_SHORT).show();
                }
            });


            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.GONE);
                    WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();

                    if (mWebBackForwardList.getCurrentIndex() > 0){
                        historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    webView.loadUrl("file:///android_asset/error.html");
                }
            });
            //Adds here

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                  webView.loadUrl(webView.getUrl());
                  refreshLayout.setColorSchemeColors(Color.BLUE);
                  new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          refreshLayout.setRefreshing(false);
                      }
                  },4*1000);

                }
            });

            if (Build.VERSION.SDK_INT < 18) {
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            }

            registerForContextMenu(webView);

            webView.setWebChromeClient(new WebChromeClient() {

                public void onPermissionRequest(final PermissionRequest request){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        request.grant(request.getResources());
                    }
                }
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    String currurl = webView.getUrl();
                    String currtitle=webView.getTitle();
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    this.openFileChooser(uploadMsg, "*/*");
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    this.openFileChooser(uploadMsg, acceptType, null);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    Main2Activity.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                            FILECHOOSER_RESULTCODE);
                }


                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                 FileChooserParams fileChooserParams) {
                    if (mUMA != null) {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(Main2Activity.this.getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        } catch (IOException ex) {
                            Log.e(TAG, "Image file creation failed", ex);
                        }
                        if (photoFile != null) {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    Intent[] intentArray;

                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
            });

        }


        if (!checkPermission()) {
            requestPermission();
        }

        // Rate Me Dialog

        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        // End Rate Me Dialog


    }

    //Cookies cleaner

    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    //Cookies cleaner

    private void speak(String word) {

        float pitch = 50 / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = 50 / 50;
        if (speed < 0.1) speed = 0.1f;
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        //double time = welcoming_word.length() / speed;
        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);

        if(Locale.getDefault().getLanguage() == "fr")
        {
            //Verification de la parole dite par l'utilisateur si c'est une question

            if (is_a_question && word.equals("oui")) {
                word = request;
                is_a_question = false;
            }

            //Fin de la verification


            if (word.equals("l'état de mon compte") || word.contains("état")) {
                //Query
                mic = false;
                is_a_question = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_OVERVIEW,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        //An action here
                                        speak(jsonObject.getString("message"));

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", user_id);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

                return;
            } else if (word.contains("ma page de profil") || word.contains("mon profil")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/profile.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Chargement de votre profil...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("page des notifications") || word.contains("mes notification") || word.contains("notification")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/notification.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Vos notifications...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("retourne a la page d'accueil") || word.contains("fil d'actualité") ||
                    word.contains("page principale") || word.contains("page d'accueil") ||
                    word.contains("fil") || word.contains("accueil")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/fil.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "&version=pegasus");
                Toast.makeText(getApplicationContext(), "chargement de votre fil d'actualite...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("ma messagerie") || word.contains("mes message") ||
                    word.contains("je veux ecrire un message") ||
                    word.contains("messagerie")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/message.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                //Toast.makeText(getApplicationContext(),"loading your feed...",Toast.LENGTH_SHORT).show();

            } else if (word.contains("message à") || word.contains("ecrire à")) {
                is_a_question = false;
                String[] content = word.split("à");
                final String username = content[1];

                //Query
                mic = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_MESSAGE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/chat.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

            }else if (word.contains("le profile de")) {
                is_a_question = false;
                String[] content = word.split("de");
                final String username = content[1];

                //Query
                mic = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEARCH_FRIENDS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/profile.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

            } else if (word.contains("mettre à jour les informations de mon profil") || word.contains("profile info") || word.contains("modifier mon profil")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/edit_profile.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading your profile infos...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("explorer le forum") || word.contains("forum") || word.contains("voir le forum")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_forums.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Chargement des forums...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("explorer le place") || word.contains("place") || word.contains("voir les places")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_places.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Chargement des forums...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("modifier mon mot de passe") || word.contains("changer mon mot de passe") || word.contains("changer de mot de passe")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/reset_password.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "chargement de la page de modification du mot de passe...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("recherche des amis") || word.contains("recherche des proches")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_users.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "chargement de personnes que vous connaissez peut-être...", Toast.LENGTH_SHORT).show();

            } else if (word.contains("je recherche une personne nommée") || word.contains("cherche pour moi") || word.contains("une personne appelee")) {
                mic = false;
                is_a_question = false;
                String username = "";
                String[] content;
                if(word.contains("moi")){
                    content = word.split("moi");
                    username = content[1];
                }else if(word.contains("nommée")){
                    content = word.split("nommée");
                    username = content[1];
                }else if(word.contains("appelée"))
                {
                    content = word.split("appelée");
                    username = content[1];
                }

                //Query
                mic = false;
                final String finalUsername = username;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEARCH_FRIENDS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/profile.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", finalUsername);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query


            } else if (word.contains("comment pouvez-vous aider") || word.contains("comment pouvez-vous m'aider") || word.contains("comment peux-tu aider")
                    || word.contains("comment peux-tu m'aider") || word.contains("tu peux m'aider")) {
                is_a_question = true;
                answer = "Oui, Actuellement, je suis là pour vous aider à naviguer dans l'application, par exemple pour passer de la page en cours à celle des notifications," +
                        "il suffit de me le dire, et si vous voulez écrire un message privé à une certaine personne" +
                        " tu dois juste me dire à quelle personne tu veux envoyer un message, mais tu dois faire attention à ton accent quand tu me dicte " +
                        "le nom du destinataire...";

            }else if (word.contains("ajouter une publication") || word.contains("créer une publication")
                    || word.contains("nouvelle publication") || word.contains("nouveau post")
                    || word.contains("ajouter quelque chose de nouveau") || word.contains("nouvelle photo")
                    ) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/create_post.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Chargement...", Toast.LENGTH_SHORT).show();

            } else if (word.contains("Comment allez-vous") || word.contains("ca va")) {

                is_a_question = true;
                answer = "Oui, je le suis, j'espère que vous aussi";

            } else if (word.contains("n'a pas été trouvé")) {

                is_a_question = true;
                answer = "Voulez-vous que je procède en ouvrant votre messagerie ?";
                request = "messagerie";

            } else if (word.contains("fermer l'application") || word.contains("Fermez l'application")
                    || word.contains("Fermer l'appli") || word.contains("aurevoir") ) {
                is_a_question = false;
                mic = false;
                answer = "souhait";
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }

                }, 5000);

            }else if(word.contains("liste d'ami") || word.contains("combien d'ami") || word.contains("mes amis et proches") || word.contains("mes proches")){

                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/friends_list.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Chargement de votre liste des amis...", Toast.LENGTH_SHORT).show();

            } else if(word.equals("souhait")){
                is_a_question = false;
                mic = false;
                if(timeOfDay >= 0 && timeOfDay < 12){
                    answer = "J'espere vous revoir bientot, Bonne journee!";
                }else if(timeOfDay >= 12 && timeOfDay < 17){
                    answer = "J'espere vous revoir bientot, Bon après-midi";
                }else if(timeOfDay >= 17 && timeOfDay < 21){
                    answer = "J'espere vous revoir bientot, Bonne soirée";
                }else if(timeOfDay >= 21 && timeOfDay < 24){
                    answer = "J'espere vous revoir bientot, Bonne nuit";
                }
            }else if(word.contains("retourne a la page precedente") || word.contains("page precedente")){

                mic = false;
                is_a_question = false;
                webView.loadUrl(historyUrl);
                Toast.makeText(getApplicationContext(), "Chargement...", Toast.LENGTH_SHORT).show();

            }else if (word.contains("quoi de neuf aujourd'hui") || word.contains("le monde aujourd'hui") || word.contains("actualité")) {
                mic = false;
                is_a_question = false;
                answer = "Veuillez patienter...je charge les actualités du jour";
                startActivity(new Intent(this,NewsActivity.class));
                Toast.makeText(getApplicationContext(), "Veuillez patienter...je charge les actualités du jour", Toast.LENGTH_SHORT).show();

            }else if (word.contains("la télé") || word.contains("france 24") || word.contains("television") || word.contains("regarder les info") || word.contains("tv")) {
                mic = false;
                is_a_question = false;
                startActivity(new Intent(this,TvActivity.class));

            }else{
                is_a_question = true;
                answer = "Je n'ai pas saisi votre requête, veuillez réessayer";
            }


            if (word == "") {

                if (!greet) {

                    if(timeOfDay >= 0 && timeOfDay < 12){
                        answer = "Bonjour  " + DataStore.getInstance(this).getUsername() + ", Comment puis-je vous aider ?";
                    }else if(timeOfDay >= 12 && timeOfDay < 17){
                        answer = "Bonjour  " + DataStore.getInstance(this).getUsername() + ", Comment puis-je vous aider ?";
                    }else if(timeOfDay >= 17 && timeOfDay < 21){
                        answer = "Bonsoir  " + DataStore.getInstance(this).getUsername() + ", Comment puis-je vous aider ?";
                    }else if(timeOfDay >= 21 && timeOfDay < 24){
                        answer = "Bonsoir  " + DataStore.getInstance(this).getUsername() + ", Comment puis-je vous aider ?";
                    }
                    greet = true;

                } else if (greet) {
                    answer = "  Puis-je faire quelque chose pour aider ?";
                }
            } else {
                if (is_a_question != true) {
                    answer = message_de_chargement;
                }
            }

        }else
        {
            //Verification de la parole dite par l'utilisateur si c'est une question

            if (is_a_question && word.equals("yes")) {
                word = request;
                is_a_question = false;
            }

            //Fin de la verification


            if (word.equals("can i have my account status") || word.equals("can i please have my account status") || word.contains("status")) {
                //Query
                mic = false;
                is_a_question = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_OVERVIEW,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        //An action here
                                        speak(jsonObject.getString("message"));

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", user_id);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

                return;
            } else if (word.contains("can you open my profile page") || word.contains("can you please open my profile page") || word.contains("profile page please") || word.contains("my profile")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/profile.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Your profile...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("can you open my notification page") || word.contains("can you please open my notification page") || word.contains("notification page please") || word.contains("notification")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/notification.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Your notifications...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("go back to the main page please") || word.contains("go back to the main page") ||
                    word.contains("Go back to my feed") || word.contains("feeds") ||
                    word.contains("main page") || word.contains("back home") || word.contains("navigate to my main page")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/fil.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "&version=pegasus");
                Toast.makeText(getApplicationContext(), "loading your feed...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("my inbox") || word.contains("my messaging") ||
                    word.contains("want to write a message") ||
                    word.contains("messenger") || word.contains("my mailbox") || word.contains("navigate to my messenger")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/message.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                //Toast.makeText(getApplicationContext(),"loading your feed...",Toast.LENGTH_SHORT).show();

            } else if (word.contains("message to") || word.contains("text to")) {
                is_a_question = false;
                String[] content = word.split("to");
                final String username = content[1];

                //Query
                mic = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_MESSAGE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        //An action here
                                        //speak(jsonObject.getString("message"));
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/chat.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

            } else if (word.contains("update my profile info") || word.contains("profile info") || word.contains("my data") || word.contains("navigate to update profile info page")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/edit_profile.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading your profile infos...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("explore forum") || word.contains("forum") || word.contains("navigate to forum")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_forums.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading forums...", Toast.LENGTH_SHORT).show();
            }else if (word.contains("explore place") || word.contains("place") || word.contains("navigate to place")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_places.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading places...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("change my password") || word.contains("modify my password") || word.contains("my password") || word.contains("password modification")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/reset_password.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "password modification page loading...", Toast.LENGTH_SHORT).show();
            } else if (word.contains("searching for friends") || word.contains("searching for relatives") || word.contains("looking for friends") || word.contains("looking for relatives")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/list_users.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading people you may know...", Toast.LENGTH_SHORT).show();

            }else if (word.contains("add a publication") || word.contains("create a publication") || word.contains("new publication") || word.contains("new post")) {
                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/create_post.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "loading...", Toast.LENGTH_SHORT).show();

            } else if (word.contains("searching for") || word.contains("looking for")) {
                mic = false;
                is_a_question = false;
                String[] content = word.split("for");
                final String username = content[1];

                //Query
                mic = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEARCH_FRIENDS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/profile.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query


            } else if (word.contains("can you help")) {

                is_a_question = true;
                answer = "I can help you navigate through the application, which page do you want me to navigate to?";

            } else if (word.contains("how can you help") || word.contains("how to use") || word.contains("how can you help me")) {
                is_a_question = true;
                answer = "Currently i'm here to help you navigate through the application, for example to navigate from a current page to the notifications one," +
                        "you just have to tell me, and if you want to write a private message to a certain person" +
                        " you just have to tell me which person you want to text to, but you have to pay attention to your accent when telling me" +
                        "the name of the recipient...";

            }else if(word.contains("friends list") || word.contains("list of friends") || word.contains("relatives list") || word.contains("list of relatives")){

                mic = false;
                is_a_question = false;
                webView.loadUrl("https://zungvi.com/android/friends_list.php?id=" + DataStore.getInstance(this).getCurrentUserId() + "");
                Toast.makeText(getApplicationContext(), "Loading your friends list...", Toast.LENGTH_SHORT).show();

            } else if (word.contains("How are you doing") || word.contains("are you okay")) {

                is_a_question = true;
                answer = "Yes i am, i hope you too";

            }else if (word.contains("open") && word.contains("profile")) {
                is_a_question = false;
                String[] content = word.split("profile");
                final String username = content[0];

                //Query
                mic = false;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEARCH_FRIENDS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    if (!jsonObject.getBoolean("error")) {
                                        mic = false;
                                        webView.loadUrl("https://zungvi.com/android/profile.php?id=" + jsonObject.getInt("id") + "");

                                    } else {
                                        answer = jsonObject.getString("message");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                speak("Something went wrong...please try again");
                                Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
                //End Query

            } else if (word.contains("has not been found")) {

                is_a_question = true;
                answer = "Do you want me to proceed by opening your messenger?";
                request = "messenger";

            } else if (word.contains("close the app")) {
                is_a_question = false;
                mic = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }

                }, 5000);

            }else if(word.contains("go back to the previous page") || word.contains("previous page")){

                mic = false;
                is_a_question = false;
                webView.loadUrl(historyUrl);
                Toast.makeText(getApplicationContext(), "loading...", Toast.LENGTH_SHORT).show();

            }else if (word.contains("what's new today") || word.contains("the world today") || word.contains("news")) {
                mic = false;
                is_a_question = false;
                answer = "Please wait...I am loading today's news";
                startActivity(new Intent(this,NewsActivity.class));
                Toast.makeText(getApplicationContext(), "Please wait...I am loading today's news", Toast.LENGTH_SHORT).show();

            }else if (word.contains("the tv") || word.contains("france 24") || word.contains("television") || word.contains("watch today's news") || word.contains("online tv")) {
                mic = false;
                is_a_question = false;
                startActivity(new Intent(this,TvActivity.class));

            }else{
                is_a_question = true;
                answer = "I did not catch your request, please try again.";
            }


            if (word == "") {
                if (!greet) {
                    if(timeOfDay >= 0 && timeOfDay < 12){
                        answer = "Good Morning  " + DataStore.getInstance(this).getUsername() + ", How can i help ?";
                    }else if(timeOfDay >= 12 && timeOfDay < 17){
                        answer = "Good Afternoon  " + DataStore.getInstance(this).getUsername() + ", How can i help ?";
                    }else if(timeOfDay >= 17 && timeOfDay < 21){
                        answer = "Good Evening  " + DataStore.getInstance(this).getUsername() + ", How can i help ?";
                    }else if(timeOfDay >= 21 && timeOfDay < 24){
                        answer = "Hey  " + DataStore.getInstance(this).getUsername() + ", How can i help ?";
                    }
                    greet = true;
                } else if (greet) {
                    answer = "  Can i do something to help?";
                }
            } else {

                if (is_a_question != true) {
                    answer = loading_message;
                }
            }

        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        } else {
            textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null);

            if(word != "") {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Mic initialization
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                        if(Locale.getDefault().getLanguage() == "fr") {

                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,  Locale.FRANCE.toString());
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Que puis-je faire ?");

                        }else{

                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

                        }

                        //Start intent
                        try {
                            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        //End initialization
                    }
                }, 6000);

            }

            word = "";
        }


    }

    public void ShowPopup(View v,String profilePhoto, String firstname, String lastname) throws IOException {

        ImageView profile;
        TextView txtclose;
        Button btnFollow;
        user_dialog.setContentView(R.layout.custompopup);
        txtclose = (TextView) user_dialog.findViewById(R.id.txtclose);
        btnFollow = (Button) user_dialog.findViewById(R.id.btnfollow);
        profile = (ImageView) user_dialog.findViewById(R.id.profile_image);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               user_dialog.dismiss();
            }
        });

        //Loading profile image
        URL url = new URL("http://zungvi.com/"+profilePhoto);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        profile.setImageBitmap(bmp);


        //End loading
        user_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        user_dialog.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
           case R.id.tv_menu:
                startActivity(new Intent(this,TvActivity.class));
                break;
           case R.id.news_menu:
                startActivity(new Intent(this,NewsActivity.class));
                break;
            case R.id.logout_menu:
                DataStore.getInstance(this).logout();
                clearCookies(getApplicationContext());
                WebStorage.getInstance().deleteAllData();
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;

           case R.id.about_menu:
                startActivity(new Intent(this, aboutActivity.class));
            break;
            case R.id.sharing_menu:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! Need to stay in touch with your loved ones, your community. Zungvi is the solution to that...Download the app at https://play.google.com/store/apps/details?id=com.social.www.zungviv2 have fun!";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                break;
        }
        return true;
    }


    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,
                new String []{Manifest.permission.CAMERA,Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //Sound receiver
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != intent){
                    ArrayList<String> result = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Toast.makeText(getApplicationContext(),result.get(0),Toast.LENGTH_SHORT).show();
                    speak(result.get(0));
                }
            break;
            } default:
                if (Build.VERSION.SDK_INT >= 21) {
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
                                }
                            }
                        }
                    }
                    mUMA.onReceiveValue(results);
                    mUMA = null;
                } else {
                    if (requestCode == FCR) {
                        if (null == mUM) return;
                        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                        mUM.onReceiveValue(result);
                        mUM = null;
                    }
                }

        }

    }
    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            showMessageOKCANCEL("You need to allow access permissions",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermission();
                                            }
                                        }
                                    });
                        }

                        if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                        {
                            showMessageOKCANCEL("You need to allow access permissions",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }

                }
                break;
        }
    }

    private void showMessageOKCANCEL(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(Main2Activity.this)
                .setMessage(message)
                .setPositiveButton("OK",okListener)
                .setNegativeButton("Cancel",null)
                .show();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Oops");
        if(Locale.getDefault().getLanguage() == "fr")
        {
            builder.setMessage("Veuillez activer les données mobile ou le wifi pour utiliser Zungvi");

        }else{
            builder.setMessage("Please switch on Mobile Data or wifi to use Zungvi");
        }
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (Build.VERSION.SDK_INT >= 21) {

            ComponentName componentName = new ComponentName(this, NotificationJobService.class);
            JobInfo info = new JobInfo.Builder(123, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(15 * 60 * 2000)
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);



        if (resultCode == JobScheduler.RESULT_SUCCESS)
        {
            Log.d(TAG1, "Job scheduled");
        }else{
            Log.d(TAG1, "Job scheduling failed");
        }

        }
    }

    @Override
    public void onBackPressed() {

        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        if(v == floatingActionButton1){
            mic = true;
            if(!greet) {
                if(Locale.getDefault().getLanguage() == "fr") {
                    Toast.makeText(getApplicationContext(), "Initilialisation de l'assistante...", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Starting the assistant...", Toast.LENGTH_LONG).show();
                }
            }
            speak("");
        }

        if(v == floatingActionButton2){

            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
            webView.loadUrl("http://www.zungvi.com/android/create_post.php?id="+DataStore.getInstance(this).getCurrentUserId());

        }

        if(v == floatingActionButton3){

            startActivity(new Intent(this,TvActivity.class));
        }

        if(v == floatingActionButton4){

            startActivity(new Intent(this,NewsActivity.class));
        }

    }
}
