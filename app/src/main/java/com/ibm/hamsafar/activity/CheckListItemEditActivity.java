package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.hamsafar.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.CheckItem;
import com.ibm.hamsafar.utils.DateUtil;

/**
 * Created by maryam on 12/19/2018.
 */
public class CheckListItemEditActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Context context = this;
    private TextInputLayout topic_layout = null;
    private TextInputLayout date_layout = null;
    private TextInputLayout time_layout = null;
    private EditText topic = null;
    private EditText date = null;
    private EditText time = null;
    private RelativeLayout time_parent = null;
    private ImageView date_picker = null;
    private ImageView time_picker = null;
    private CheckBox checkBox = null;
    private Animation animation_appear = null;
    private Animation animation_disappear = null;
    private CheckItem checkItem;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private Button save = null;
    private Button cancel = null;


    public void onCreate( Bundle bundle ) {
        super.onCreate( bundle );
        setContentView( R.layout.checklist_item_edit);

        //initialise
        checkItem = new CheckItem();
        if( getIntent().hasExtra("check_item")) {
            checkItem = (CheckItem) getIntent().getSerializableExtra("check_item");
            topic.setText( checkItem.getTopic() );
            date.setText( checkItem.getDate() );
            if( !checkItem.getTime().equals("") ) {
                time.setText(checkItem.getTime());
                checkBox.setChecked( true );
            }
            else
                checkBox.setChecked( false );
        }
        else {
            date.setText(DateUtil.getCurrentDate() );
            checkBox.setChecked( false );
        }


        topic_layout = findViewById(R.id.cl_dialog_topic_layout);
        date_layout = findViewById(R.id.cl_dialog_date_layout);
        time_layout = findViewById(R.id.cl_dialog_time_layout);
        topic = findViewById(R.id.cl_dialog_topic);
        date = findViewById(R.id.cl_dialog_date);
        time = findViewById(R.id.cl_dialog_time);
        time_parent = findViewById(R.id.cl_dialog_time_parent);
        date_picker = findViewById(R.id.cl_dialog_date_picker);
        time_picker = findViewById(R.id.cl_dialog_time_picker);
        checkBox = findViewById(R.id.cl_dialog_cb);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        save = findViewById(R.id.cl_edit_save);
        cancel = findViewById(R.id.cl_edit_cancel);

        animation_appear = AnimationUtils.loadAnimation(CheckListItemEditActivity.this, R.anim.animation_fade_in);
        animation_disappear = AnimationUtils.loadAnimation(CheckListItemEditActivity.this, R.anim.animation_fade_out);


        clearError();


        toolbarBack.setOnClickListener(view -> {
            onBackPressed();
        });

        toolbarTitle.setText( getResources().getString( R.string.cl_edit_title));

        if( !checkBox.isChecked() ) {
            time_parent.setVisibility( View.GONE );
        }

        checkBox.setOnClickListener(view -> {
            if( checkBox.isChecked() ) {
                time_parent.setVisibility(View.VISIBLE);
                time_parent.startAnimation( animation_appear );
            }
            else {
                //time_parent.startAnimation( animation_disappear );
                time_parent.setVisibility(View.GONE);
            }
        });

        date_picker.setOnClickListener(view -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    CheckListItemEditActivity.this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
        });


        time_picker.setOnClickListener(view -> {
            PersianCalendar now = new PersianCalendar();
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                    CheckListItemEditActivity.this,
                    now.get(PersianCalendar.HOUR_OF_DAY),
                    now.get(PersianCalendar.MINUTE),
                    true
            );
            timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
        });

        cancel.setOnClickListener(view -> finish());


        save.setOnClickListener(view -> {
            //update db info
            finish();
        });
    }


    private void clearError() {
        topic_layout.setError(null);
        date_layout.setError(null);
        time_layout.setError(null);
    }


    @Override
    public void onDateSet(com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int moth = monthOfYear + 1;
        //check date validation
        date.setText(year + "/" + moth + "/" + dayOfMonth);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time =  hourString + ":" + minuteString;
        this.time.setText(time);
    }
}
