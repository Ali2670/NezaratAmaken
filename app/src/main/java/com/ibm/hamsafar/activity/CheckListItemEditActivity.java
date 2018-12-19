package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.CheckItem;

/**
 * Created by maryam on 12/19/2018.
 */
public class CheckListItemEditActivity extends Activity {

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


    public void onCreate( Bundle bundle ) {
        super.onCreate( bundle );
        setContentView( R.layout.checklist_item_edit);



        animation_appear = AnimationUtils.loadAnimation(CheckListItemEditActivity.this, R.anim.animation_fade_in);
        animation_disappear = AnimationUtils.loadAnimation(CheckListItemEditActivity.this, R.anim.animation_fade_out);
    }





    //topic edit dialog
    public void openEditDialog( CheckItem checkItem ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setCancelable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemDialog = inflater.inflate(R.layout.checklist_item_edit, null);
        builder.setView(itemDialog);
        topic_layout = itemDialog.findViewById(R.id.cl_dialog_topic_layout);
        date_layout = itemDialog.findViewById(R.id.cl_dialog_date_layout);
        time_layout = itemDialog.findViewById(R.id.cl_dialog_time_layout);
        topic = itemDialog.findViewById(R.id.cl_dialog_topic);
        date = itemDialog.findViewById(R.id.cl_dialog_date);
        time = itemDialog.findViewById(R.id.cl_dialog_time);
        time_parent = itemDialog.findViewById(R.id.cl_dialog_time_parent);
        date_picker = itemDialog.findViewById(R.id.cl_dialog_date_picker);
        time_picker = itemDialog.findViewById(R.id.cl_dialog_time_picker);
        checkBox = itemDialog.findViewById(R.id.cl_dialog_cb);

        //initialise
        topic.setText( checkItem.getTopic() );
        date.setText( checkItem.getDate() );
        if( !checkItem.getTime().equals("") ) {
            time.setText(checkItem.getTime());
            checkBox.setChecked( true );
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        if( !checkBox.isChecked() ) {
            time_parent.setVisibility( View.GONE );
        }

        checkBox.setOnClickListener(view -> {
            if( checkBox.isChecked() ) {
                time_parent.setVisibility(View.VISIBLE);
                time_parent.startAnimation( animation_appear );
            }
            else {
                time_parent.startAnimation( animation_disappear );
                time_parent.setVisibility(View.GONE);
            }
        });
    }

}
