package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.hamsafar.R;

public class NewTripActivity extends Activity {

    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private TextInputLayout startLayout = null;
    private EditText startText = null;
    private TextInputLayout desLayout = null;
    private EditText desText = null;
    private TextInputLayout startDateLayout = null;
    private EditText startDateText = null;
    private TextInputLayout endDateLayout = null;
    private EditText endDateText = null;
    private TextInputLayout transLayout = null;
    private EditText transText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trip);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        startLayout = findViewById(R.id.trip_port_layout);
        startText = findViewById(R.id.trip_port);
        desLayout = findViewById(R.id.trip_destination_layout);
        desText = findViewById(R.id.trip_destination);
        startDateLayout = findViewById(R.id.trip_start_date_layout);
        startDateText = findViewById(R.id.trip_start_date);
        endDateLayout = findViewById(R.id.trip_end_date_layout);
        endDateText = findViewById(R.id.trip_end_date);
        transLayout = findViewById(R.id.trip_transport_layout);
        transText = findViewById(R.id.trip_transport);



        toolbarTitle.setText(getResources().getString(R.string.trip_title));

        toolbarBack.setOnClickListener(view -> onBackPressed());

        clearError();

        startLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startLayout.getEditText(), startLayout));
        desLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(desLayout.getEditText(), desLayout));
        startDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(startDateLayout.getEditText(), startDateLayout));
        endDateLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(endDateLayout.getEditText(), endDateLayout));
        transLayout.getEditText().addTextChangedListener(
                new EnrolActivity.GenericTextWatcher(transLayout.getEditText(), transLayout));


        //get user location
        startText.setText("تهران");


    }


    /*
    * show list dialog
    *
    * */
    private void showListDialog(final EditText editText, String title, String[] items ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewTripActivity.this);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
        listTitle.setText( title );
        ListView listView = listViewDialog.findViewById(R.id.dialogList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_expandable_list_item, items);
        listView.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String value = ((TextView) view).getText().toString();
            editText.setText( value );
            dialog.dismiss();
        });
    }




    class GenericTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout layout;

        private GenericTextWatcher(View view, TextInputLayout layout) {
            this.view = view;
            this.layout = layout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0)
                layout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
            layout.setError(null);
        }
    }

    private void clearError() {
        startLayout.setError(null);
        desLayout.setError(null);
        startDateLayout.setError(null);
        endDateLayout.setError(null);
        transLayout.setError(null);
    }



    private boolean checkInternetConnection() {
        //if connected return true
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable() || !info.isConnected()) {
            return false;
        }
        return true;
    }

}
