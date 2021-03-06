package com.ibm.hamsafar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.LoginRequest;
import hamsafar.ws.response.ConfirmLoginResponse;
import hamsafar.ws.response.LoginResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends Activity {

    private Context context = this;
    private TextInputLayout mobileLayout = null;
    private EditText mobile = null;
    private TextInputLayout codeLayout = null;
    private EditText code = null;
    private Button resend = null;
    private Button login = null;
    private TextView textViewTime = null;
    private ProgressBar progressBarCircle = null;
    private RelativeLayout timerLayout = null;
    private LinearLayout blurLayout = null;
    private SharedPreferences sharedPreferences;

    //check login button to send activation code or check activation code
    private boolean loggedIn = false;

    private String phoneNum;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mobileLayout = findViewById(R.id.login_mobile_layout);
        mobile = findViewById(R.id.login_mobile);
        codeLayout = findViewById(R.id.login_code_layout);
        code = findViewById(R.id.login_code);
        resend = findViewById(R.id.login_resend_code);
        login = findViewById(R.id.login_login_btn);
        textViewTime = findViewById(R.id.textViewTime);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        timerLayout = findViewById(R.id.timer_layout);
        blurLayout = findViewById(R.id.login_blur_layout);

        mobileLayout.setError(null);
        codeLayout.setError(null);

        codeLayout.setVisibility(View.GONE);
        resend.setVisibility(View.GONE);
        timerLayout.setVisibility(View.GONE);

        login.setOnClickListener(view -> {
            if (!loggedIn) {
                String mobileNumber = mobile.getText().toString().trim();
                if (mobileNumber.equals("")) {
                    mobileLayout.setError(getResources().getString(R.string.login_mobile_empty_error));
                } else if (mobileNumber.length() < 11) {
                    mobileLayout.setError(getResources().getString(R.string.login_mobile_correct_error));
                } else {
                        mobileLayout.setError(null);
                        phoneNum = mobile.getText().toString();
                        login(phoneNum);
                }
            } else {
                if (code.getText().toString().trim().equals("")) {
                    codeLayout.setError(getResources().getString(R.string.login_empty_activation_code_error));
                } else {
                        sendActivationCode(phoneNum, code.getText().toString().trim());
                        //checkActivationCode(Integer.parseInt(code.getText().toString().trim()));
                }
            }
        });

        resend.setOnClickListener(view -> {
            timerLayout.setVisibility(View.VISIBLE);
            startTimer();
            resend.setVisibility(View.GONE);
            phoneNum = mobile.getText().toString().trim();
            login(phoneNum);
        });

    }

    private void startTimer() {

        int time = 1;
        final long timeCountInMilliSeconds = time * 60 * 1000;
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
        CountDownTimer countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
                progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
                timerLayout.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }

    //send mobile to get activation code
    private void login(String phoneNum) {
        LoginRequest request = new LoginRequest();
        request.setPhoneNo(phoneNum);

        TaskCallBack<Object> loginCallBack = result -> {
            LoginResponse ress = JsonCodec.toObject((Map) result, LoginResponse.class);
            codeLayout.setVisibility(View.VISIBLE);
            timerLayout.setVisibility(View.VISIBLE);
            startTimer();
            loggedIn = true;
        };

        new ListHttp(loginCallBack, this, null, ServiceNames.DO_LOGIN, false).execute(request);
    }


    //check activation code
    private void sendActivationCode(String phoneNum, String activationCode) {
        LoginRequest request = new LoginRequest();
        request.setPhoneNo(phoneNum);
        request.setConfirmCode(activationCode);

        TaskCallBack<Object> confirmLoginCallBack = result -> {
            ConfirmLoginResponse ress = JsonCodec.toObject((Map) result, ConfirmLoginResponse.class);

            //do if there is no exc
            codeLayout.setError(null);
            savePreferences("user_id", ress.getUserId() );
            launchEnrolScreen();
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(confirmLoginCallBack, this, null, ServiceNames.CONFIRM_LOGIN, false);
        list.execute(request);

    }


    private void launchEnrolScreen() {
        Intent intent = new Intent(LoginActivity.this, EnrolActivity.class);
        startActivity(intent);
        finish();
    }

    private void savePreferences(String key, Integer value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

}
