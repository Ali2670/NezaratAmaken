package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.hamsafar.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.CheckItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.ChecklistItemDto;
import hamsafar.ws.common.ReminderDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.SubmitTripChecklistRequest;
import hamsafar.ws.response.SubmitChecklistResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/19/2018.
 */
public class CheckListItemAddActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Context context = this;
    private TextInputLayout topic_layout = null;
    private TextInputLayout date_layout = null;
    private TextInputLayout time_layout = null;
    private EditText topic = null;
    private EditText date = null;
    private EditText time = null;
    private CheckBox checkBox = null;
    private Animation animation_appear = null;
    private CheckItem checkItem;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private Button save = null;
    private Button cancel = null;
    //private static boolean add = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.checklist_item_edit);


        topic_layout = findViewById(R.id.cl_dialog_topic_layout);
        date_layout = findViewById(R.id.cl_dialog_date_layout);
        time_layout = findViewById(R.id.cl_dialog_time_layout);
        topic = findViewById(R.id.cl_dialog_topic);
        date = findViewById(R.id.cl_dialog_date);
        time = findViewById(R.id.cl_dialog_time);
        checkBox = findViewById(R.id.cl_dialog_cb);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        save = findViewById(R.id.cl_edit_save);
        cancel = findViewById(R.id.cl_edit_cancel);

        animation_appear = AnimationUtils.loadAnimation(CheckListItemAddActivity.this, R.anim.animation_fade_in);


        clearError();


        toolbarBack.setOnClickListener(view -> {
            onBackPressed();
        });

        toolbarTitle.setText(getResources().getString(R.string.cl_edit_title));

        if (!checkBox.isChecked()) {
            time_layout.setVisibility(View.GONE);
            date_layout.setVisibility( View.GONE );
        }

        checkBox.setOnClickListener(view -> {
            if (checkBox.isChecked()) {
                time_layout.setVisibility(View.VISIBLE);
                time_layout.startAnimation(animation_appear);
                date_layout.setVisibility(View.VISIBLE);
                date_layout.startAnimation(animation_appear);
            } else {
                //time_layout.startAnimation( animation_disappear );
                time_layout.setVisibility(View.GONE);
                date_layout.setVisibility( View.GONE );
            }
        });

        date.setOnClickListener(view -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    CheckListItemAddActivity.this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
        });


        time.setOnClickListener(view -> {
            PersianCalendar now = new PersianCalendar();
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                    CheckListItemAddActivity.this,
                    now.get(PersianCalendar.HOUR_OF_DAY),
                    now.get(PersianCalendar.MINUTE),
                    true
            );
            timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
        });

        cancel.setOnClickListener(view -> finish());


        //use add to check whether to update item or add new
        save.setOnClickListener(view -> {
            saveChanges();
        });
    }

    //save button action
    private void saveChanges() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        SubmitTripChecklistRequest request = new SubmitTripChecklistRequest();
        request.setUserId(user_id);

        //fill items
        List<ChecklistItemDto> listDB = new ArrayList<>();
        ChecklistItemDto checklistItemDto = new ChecklistItemDto();
        checklistItemDto.setUserId(user_id);
        checklistItemDto.setTitle(topic.getText().toString().trim());
        checklistItemDto.setFixedTitle(topic.getText().toString().trim());

        if (checkItem.isChecked())
            checklistItemDto.setStatus((byte) 1);
        if (!checkItem.isChecked())
            checklistItemDto.setStatus((byte) 0);

        String date_st = date.getText().toString().trim();
        String time_st = time.getText().toString().trim();
        if( checkBox.isChecked() && !date_st.equals("") && !time_st.equals("") ) {

            checklistItemDto.setReminderFlag((byte) 1);

            //create reminder
            ReminderDto reminderDto = new ReminderDto();
            reminderDto.setStartDate(date.getText().toString().trim());
            reminderDto.setStartTime(time.getText().toString().trim());
            reminderDto.setReminderType((byte) 0);
            reminderDto.setRemindDate(date.getText().toString().trim());
            reminderDto.setRemindTime(time.getText().toString().trim());
            reminderDto.setStatus((byte) 0);

            checklistItemDto.setReminderDto(reminderDto);
        }
        else {
            checklistItemDto.setReminderFlag((byte) 0);
        }

        listDB.add(checklistItemDto);
        request.setChecklistDtoList(listDB);

        TaskCallBack<Object> submitChecklistResponse = result -> {
            SubmitChecklistResponse ress = JsonCodec.toObject((Map) result, SubmitChecklistResponse.class);

            if (ress.getSuccessful()) {
                Toast.makeText(context, getResources().getString(R.string.save_success_message), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, getResources().getString(R.string.save_failure_message), Toast.LENGTH_SHORT).show();
            }

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitChecklistResponse, this, null, ServiceNames.SUBMIT_CHECKLIST, false);
        list.execute(request);
    }


    private void clearError() {
        topic_layout.setError(null);
        date_layout.setError(null);
        time_layout.setError(null);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int moth = monthOfYear + 1;
        //check date validation
        date.setText(year + "/" + String.format("%02d", moth) + "/" + String.format("%02d", dayOfMonth));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;
        this.time.setText(time);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckListItemAddActivity.this);
        builder.setMessage(getResources().getString(R.string.exit_message));
        builder.setPositiveButton(getResources().getString(R.string.exit_save_changes),
                (dialogInterface, i) -> {
                    saveChanges();
                    finish();
                });
        builder.setNeutralButton(getResources().getString(R.string.exit_cancel),
                (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
        builder.setNegativeButton(getResources().getString(R.string.exit_discard),
                (dialogInterface, i) -> {
                    finish();
                });
        builder.show();
    }
}
