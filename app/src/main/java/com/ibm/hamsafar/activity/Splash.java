package com.ibm.hamsafar.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ibm.hamsafar.Application.AppStart;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.services.PcService;
import com.ibm.hamsafar.utils.Constants;
import com.ibm.hamsafar.utils.Tools;

import java.util.Random;

public class Splash extends AppCompatActivity {
    public static final int progress_bar_type = 0;
    private final static int TIMER_INTERVAL = 2000; // 2 sec
    private static SplashTimer timer;
    PcService service;
    AlertDialog alertDialog;
    String fileName;
    boolean canBeGo = false;
    boolean canBeGoPerm = false;
    private boolean activityStarted;
    private Button retryBtn;
    private boolean mWasGetContentIntent;
    private ProgressBar progressBar;
    private ProgressDialog pDialog;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        Intent intent = getIntent();
        mWasGetContentIntent = intent.getAction().equals(
                Intent.ACTION_GET_CONTENT);
        Random r = new Random();
        int rand = r.nextInt(6);
      /*  ActivityCompat.requestPermissions(Splash.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.VIBRATE, Manifest.permission.INTERNET


                },
                1);*/

        setContentView(R.layout.activity_splash);
//        StartAnimations();
        startService(new Intent(AppStart.getContext(), PcService.class));
    /*    try {
            String pusheId = Tools.getDefaults(Constants.PusheId);
            if (pusheId == null || pusheId.trim().equals("")) {
                Pushe.initialize(AppStart.getContext(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        setContentView(R.layout.activity_splash);
        ImageView iv = (ImageView) findViewById(R.id.logoAll);
        retryBtn = findViewById(R.id.retryBtn);
        progressBar = findViewById(R.id.progressBar);

        if (rand == 0) {
            iv.setImageResource(R.drawable.splash1);
        } /*else if (rand == 1) {
            iv.setImageResource(R.drawable.splash2);
        } else if (rand == 2) {
            iv.setImageResource(R.drawable.splash3);

        } else if (rand == 3) {
            iv.setImageResource(R.drawable.splash4);

        } else if (rand == 4) {
            iv.setImageResource(R.drawable.splash5);

        } else if (rand == 5) {
            iv.setImageResource(R.drawable.splash6);

        } else {
            iv.setImageResource(R.drawable.splash1);

        }*/

        StartAnimations();

        timer = new SplashTimer();
        timer.sendEmptyMessageDelayed(1, TIMER_INTERVAL);
        Tools.setDefaults("ver", Tools.version);
 /*    Tools.SendPm();
        ListHttp ver = new ListHttp();
        ver.execute();
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryBtn.setVisibility(View.GONE);
                ListHttp ver = new ListHttp();
                ver.execute();
            }
        });*/
        canBeGoPerm = true;
        canBeGo = true;
//        !!!!!
        GoToMain();
    }

    private void startHomePageActivity() {


        if (activityStarted) {
            return;
        }
        activityStarted = true;


    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        ImageView iv2 = (ImageView) findViewById(R.id.logosplash2);
        iv2.clearAnimation();
        iv2.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translat);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logosplash);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
/*

    private void ShowDialog(final LastVersion obj, String version) {


        String currentVersion = Tools.getStringByName("currentVersion") + version;
        String newVersion = Tools.getStringByName("newVersion") + obj.getVersion();
        String text = Tools.getStringByName("ContentVersion") + "\n" + currentVersion + "\n" + newVersion;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        alertDialogBuilderUserInput.setView(mView);
        alertDialogBuilderUserInput.setCancelable(false);
      */
/*  alertDialogBuildSerUserInput
                .setTitle(Tools.getStringByName("updateProfile"))
                .setMessage(Tools.getStringByName(text));*//*

        Button bt = mView.findViewById(R.id.signUp);
        TextView textView = mView.findViewById(R.id.dialogClose);
        textView.setVisibility(View.INVISIBLE);
        TextView message = mView.findViewById(R.id.title);
        bt.setVisibility(View.GONE);
        message.setText(text);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(Tools.getStringByName("updateProfile"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(Splash.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
                                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.VIBRATE, Manifest.permission.INTERNET
                                },
                                1);
                        fileName = "ver_" + obj.getVersion() + ".apk";
                        String url = Tools.URL_APK[Tools.SERVER_ID] + fileName;
                        new DownloadFileFromURL().execute(url);
                    }
                })
                .setNegativeButton(Tools.getStringByName("skipupdate")
                        ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                                GoToMain();

                            }
                        });

        alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
        retryBtn.setVisibility(View.VISIBLE);
        if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
        if (alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
    }
*/

/*    public void CheckVersion(LastVersion obj) {
        if (obj.getVersion() != null && (
                Tools.version == null || Tools.version.compareTo(obj.getVersion()) < 0)) {
            ShowDialog(obj, Tools.version);
        } else {
            GoToMain();
        }
    }*/

    private void GoToMain() {
        if (!canBeGo || !canBeGoPerm) {
            new SplashTimerEnd().sendEmptyMessageDelayed(1, TIMER_INTERVAL - 600);
        } else {
            GoToMainPage();
        }
    }

    private void GoToMainPage() {
        String userId = Tools.getDefaults(Constants.UserId);
        String sessionId = Tools.getDefaults(Constants.SessionId);
        if (userId != null && userId.trim().length() > 1 && sessionId != null && sessionId.length() > 0) {
            String first = Tools.getDefaults("first", Splash.this);

            if (first == null || first.trim().equals("")) {
                Tools.setDefaults("first", "false", Splash.this);
//                startActivity(new Intent(Splash.this, Main.class));

                finish();
            } else {
//                startActivity(new Intent(Splash.this, Main.class));
                finish();
            }
        } else {
            startActivity(new Intent(Splash.this, IntroActivity.class));
            finish();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this, R.style.MyProgressDialog);
                pDialog.setMessage("درحال دانلود نسخه جدید ... منتظر بمانید");
                pDialog.setIndeterminate(false);

                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();

                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Splash.this, "Permission denied !!!", Toast.LENGTH_SHORT).show();
                }
                canBeGoPerm = true;

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    final class SplashTimer extends Handler {
        @Override
        public void handleMessage(Message msg) {
            post(new Runnable() {

                public void run() {
                    canBeGo = true;

//                    startHomePageActivity();
                }
            });
        }
    }

    final class SplashTimerEnd extends Handler {
        @Override
        public void handleMessage(Message msg) {
            post(new Runnable() {

                public void run() {
                    GoToMainPage();
                }
            });
        }
    }
/*
    private class ListHttp extends AsyncTask<CatItemsListSend, Void, WsResult> {


        Toast toast;

        @Override
        protected WsResult doInBackground(CatItemsListSend... params) {
            WsResult result;

            try {

                CookLastVersion obj = RestCaller.LastVersion("LastVersion", (Object[]) params);


                if (obj == null) {
                    result = new WsResult(null, NULL_VALUE_RETURN);

                } else {

                    result = new WsResult(obj, RESULT_IS_OK);
                }
            } catch (GenericBusinessException e) {
                result = new WsResult(null, e.getErrCode());

            } catch (ConnectException e) {
                result = new WsResult(null, CONNECTION_TIMEOUT_EXCEPTION);
            }
            return result;

        }

        @Override
        protected void onPostExecute(WsResult result) {

            try {

                if (result.getErr().equals(RESULT_IS_OK)) {
//                    recordNotFound.setVisibility(View.INVISIBLE);

                    CookLastVersion obj = (CookLastVersion) result.getObject();
                    if (obj != null) {
                        CheckVersion(obj);
                    } else {

                        Err(result.getErr());
                    }
                } else {
                    Err(result.getErr());
                }

            } catch (Exception e) {
                Err(UPDATE_EXCEPTION);

            }


        }

        private void Err(int i) {
            retryBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            String errStr = Tools.getStringByName("Exc." + i);
            if (toast != null) {
                toast.cancel();
            }
            Tools.ShowDialog(Splash.this, Tools.getStringByName("Error"), errStr);

            *//*
            toast = Toast.makeText(AppStart.getContext(), errStr, Toast.LENGTH_LONG);
            toast.show();
*//*
            if (ExceptionConstants.REST_INVALID_SESSION_ID == i) {
//                startActivity(new Intent(CategoryList.this, LoginActivity.class));
//                finish();
            }
        }


        @Override
        protected void onPreExecute() {
//            recordNotFoundTv.setVisibility(View.INVISIBLE);
          *//*  if (loadMore == null) {
                Toast.makeText(getActivity(), "err . drop down to refresh", Toast.LENGTH_SHORT).show();

            }
*//*
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, Integer, String> {

        *//**
     * Before starting background thread
     * Show Progress Bar Dialog
     *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        *//**
     * Downloading file in background thread
     *//*
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection conection = null;
            try {
                URL url = new URL(f_url[0]);
                conection = (HttpURLConnection) url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity"); // <--- Add this line
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                input = conection.getInputStream();

                // Output stream to write file
//                OutputStream output = openFileOutput(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName).getAbsolutePath(), Context.MODE_PRIVATE);
                String u = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
                output = new FileOutputStream(new File(u));
                byte data[] = new byte[4096];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (lenghtOfFile > 0) // only if total length is known
                        publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                return "EXC." + e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (conection != null)
                    conection.disconnect();
            }

            return null;
        }

        *//**
     * Updating progress bar
     *//*
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(progress[0]);
        }

        *//**
     * After completing background task
     * Dismiss the progress dialog
     **//*
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            try {
                dismissDialog(progress_bar_type);
                if (file_url == null || !file_url.startsWith("EXC")) {

                    // Displaying downloaded image into image view
                    // Reading image path from sdcard
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                    startActivity(intent);
                    finish();
                } else {
                    Tools.ShowDialog(Splash.this, Tools.getStringByName("Error"), Tools.getStringByName("ErrInUpdate"));

                    retryBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            } catch (Exception e) {
                Tools.ShowDialog(Splash.this, Tools.getStringByName("Error"), Tools.getStringByName("ErrInUpdate"));

                progressBar.setVisibility(View.GONE);
            }
        }

    }*/
}
