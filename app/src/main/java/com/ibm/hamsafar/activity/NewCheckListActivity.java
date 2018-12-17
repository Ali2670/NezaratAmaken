package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;

public class NewCheckListActivity extends Activity {

    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private RecyclerView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_check_list);

        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);

        toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText( getResources().getString(R.string.check_list_title));



    }

}
