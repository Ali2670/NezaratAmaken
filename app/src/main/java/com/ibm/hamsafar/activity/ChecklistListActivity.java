package com.ibm.hamsafar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.CheckItemAdapter;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.CheckItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hamsafar.ws.common.ChecklistItemDto;
import hamsafar.ws.common.ReminderDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.GetGeneralCheckList;
import hamsafar.ws.request.SubmitTripChecklistRequest;
import hamsafar.ws.response.GetChecklistResponse;
import hamsafar.ws.response.SubmitChecklistResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 12/24/2018.
 */
public class ChecklistListActivity extends Activity {

    private Context context = this;
    /*private Button toolbarBack = null;
    private TextView toolbarTitle = null;*/
    private TextView toolbarSave = null;
    private TextView toolbarCancel = null;
    private RecyclerView recyclerView = null;
    private FloatingActionButton add = null;
    private LinearLayoutManager linearLayoutManager = null;
    private CheckItemAdapter adapter;
    private List<CheckItem> listData;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.checklist_view);

        /*toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);*/
        toolbarSave = findViewById(R.id.toolbar_save);
        toolbarCancel = findViewById(R.id.toolbar_cancel);
        recyclerView = findViewById(R.id.cl_view_recycler_view);
        add = findViewById(R.id.cl_view_add_item);


        /*toolbarBack.setOnClickListener(view -> onBackPressed());

        toolbarTitle.setText(getResources().getString(R.string.cl_list_title));*/

        /*linearLayoutManager = new LinearLayoutManager(ChecklistListActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getChecklistList();
        adapter = new CheckItemAdapter(context, listData);
        recyclerView.setAdapter(adapter);*/

        getChecklistList();

        add.setOnClickListener(view -> startActivity(new Intent(ChecklistListActivity.this, CheckListItemEditActivity.class)));

        toolbarCancel.setOnClickListener(view -> finish());

        toolbarSave.setOnClickListener(view -> {
            saveEditedCheckList();
        });

    }

    private void getChecklistList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        GetGeneralCheckList request = new GetGeneralCheckList();
        request.setUserId(user_id);

        TaskCallBack<Object> getChecklistResponse = result -> {
            GetChecklistResponse ress = JsonCodec.toObject((Map) result, GetChecklistResponse.class);
            List<ChecklistItemDto> listDB;
            listDB = ress.getChecklistItemDtoList();
            setList(listDB);
            linearLayoutManager = new LinearLayoutManager(ChecklistListActivity.this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new CheckItemAdapter(context, listData);
            recyclerView.setAdapter(adapter);

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(getChecklistResponse, this, null, ServiceNames.GET_CHECKLIST, false);
        list.execute(request);
    }


    private void setList(List<ChecklistItemDto> list) {
        listData = new ArrayList<>();
        int size = list.size() - 1;
        for (int i = size; i >= 0; i--) {
            CheckItem item = new CheckItem();
            item.setTopic(list.get(i).getTitle());
            item.setId(list.get(i).getId());
            item.setDate("");
            item.setTime("");
            item.setTripId(list.get(i).getTripId());
            int statue = list.get(i).getStatus();
            if (statue == 0) {
                item.setChecked(false);
            } else
                item.setChecked(true);

            item.setReminderFlag(list.get(i).getReminderFlag());

            if (list.get(i).getReminderDto() != null) {
                item.setReminderDto(list.get(i).getReminderDto());
                item.setDate(list.get(i).getReminderDto().getRemindDate());
                item.setTime(list.get(i).getReminderDto().getRemindTime());
            }

            listData.add(item);
        }
    }


    //save checklist items after edit, delete and update
    private void saveEditedCheckList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer user_id = sharedPreferences.getInt("user_id", 0);

        SubmitTripChecklistRequest request = new SubmitTripChecklistRequest();
        request.setUserId(user_id);

        //fill items
        List<ChecklistItemDto> listDB = new ArrayList<>();
        for (CheckItem checkItem : listData) {
            ChecklistItemDto checklistItemDto = new ChecklistItemDto();
            checklistItemDto.setUserId(user_id);
            if (checkItem.getId() != null) {
                checklistItemDto.setId(checkItem.getId());
            }
            checklistItemDto.setTitle(checkItem.getTopic());
            checklistItemDto.setFixedTitle(checkItem.getTopic());

            if (checkItem.isChecked())
                checklistItemDto.setStatus((byte) 1);
            if (!checkItem.isChecked())
                checklistItemDto.setStatus((byte) 0);


            String date_st = checkItem.getDate();
            String time_st = checkItem.getTime();
            if( !date_st.equals("") && !time_st.equals("") ) {

                checklistItemDto.setReminderFlag((byte) 1);

                //create reminder
                ReminderDto reminderDto = new ReminderDto();
                reminderDto.setStartDate(date_st);
                reminderDto.setStartTime(time_st);
                reminderDto.setReminderType((byte) 0);
                reminderDto.setRemindDate(date_st);
                reminderDto.setRemindTime(time_st);
                reminderDto.setStatus((byte) 0);

                checklistItemDto.setReminderDto(reminderDto);
            }
            else {
                checklistItemDto.setReminderFlag((byte) 0);
            }

            listDB.add(checklistItemDto);
        }
        request.setChecklistDtoList(listDB);

        TaskCallBack<Object> submitChecklistResponse = result -> {
            SubmitChecklistResponse ress = JsonCodec.toObject((Map) result, SubmitChecklistResponse.class);

            if (ress.getSuccessful()) {
                Toast.makeText(context, getResources().getString(R.string.save_success_message), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, getResources().getString(R.string.save_failure_message), Toast.LENGTH_SHORT).show();
            }

        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitChecklistResponse, this, null, ServiceNames.SUBMIT_CHECKLIST, false);
        list.execute(request);
    }


    @Override
    public void onResume(){
        super.onResume();

        getChecklistList();
    }


}