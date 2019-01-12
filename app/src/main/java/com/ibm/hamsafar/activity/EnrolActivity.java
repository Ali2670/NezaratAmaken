package com.ibm.hamsafar.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.UserInfo;
import com.ibm.hamsafar.utils.DateUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Map;

import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.SubmitProfileRequest;
import hamsafar.ws.response.SubmitProfileResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EnrolActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Context context = this;
    private CircularImageView photo = null;
    private TextInputLayout idCodeLayout = null;
    private EditText idCode = null;
    private TextInputLayout nameLayout = null;
    private EditText name = null;
    private TextInputLayout lastNameLayout = null;
    private EditText lastName = null;
    private TextInputLayout birthDateLayout = null;
    private EditText birthDate = null;
    private Button save = null;
    private UserInfo userInfo = new UserInfo();
    private LinearLayout parent_layout = null;

    private Button toolbarBack = null;
    private TextView toolbarTitle = null;

    //handle image
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.enrol);

        photo = findViewById(R.id.enrol_profile_photo);
        idCodeLayout = findViewById(R.id.enrol_id_code_layout);
        idCode = findViewById(R.id.enrol_id_code);
        nameLayout = findViewById(R.id.enrol_name_layout);
        name = findViewById(R.id.enrol_name);
        lastNameLayout = findViewById(R.id.enrol_last_name_layout);
        lastName = findViewById(R.id.enrol_last_name);
        birthDateLayout = findViewById(R.id.enrol_birth_date_layout);
        birthDate = findViewById(R.id.enrol_birth_date);
        save = findViewById(R.id.enrol_save_btn);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_text);
        parent_layout = findViewById(R.id.enrol_parent);

        clearError();

        idCodeLayout.getEditText().addTextChangedListener(
                new GenericTextWatcher(idCodeLayout.getEditText(), idCodeLayout));
        nameLayout.getEditText().addTextChangedListener(
                new GenericTextWatcher(nameLayout.getEditText(), nameLayout));
        lastNameLayout.getEditText().addTextChangedListener(
                new GenericTextWatcher(lastNameLayout.getEditText(), lastNameLayout));
        birthDateLayout.getEditText().addTextChangedListener(
                new GenericTextWatcher(birthDateLayout.getEditText(), birthDateLayout));

        toolbarTitle.setText( getResources().getString(R.string.enrol_title ));

        toolbarBack.setOnClickListener(view -> onBackPressed());


        save.setOnClickListener(view -> {
            setUserInfo();
            boolean hasError = false;
            //check id code
            if (userInfo.getIdCode().equals("")) {
                idCodeLayout.setError(getResources().getString(R.string.Exc_700001));
                hasError = true;
            }
            else if (!ValidateCodeMeli.checkCocdeMeli(userInfo.getIdCode())) {
                idCodeLayout.setError(getResources().getString(R.string.Exc_700002));
                hasError = true;
            }
            else if (userInfo.getIdCode().length() < 10) {
                idCodeLayout.setError(getResources().getString(R.string.Exc_700002));
                hasError = true;
            }

            //check name
            if (userInfo.getFirstName().equals("")) {
                nameLayout.setError(getResources().getString(R.string.Exc_700003));
                hasError = true;
            }

            //check last name
            if (userInfo.getLastName().equals("")) {
                lastNameLayout.setError(getResources().getString(R.string.Exc_700004));
                hasError = true;
            }

            //check birth date
            if (userInfo.getBirthDate().equals("")) {
                birthDateLayout.setError(getResources().getString(R.string.Exc_700005));
                hasError = true;
            }
            if( invalidBirthDate() ) {
                birthDateLayout.setError(getResources().getString(R.string.Exc_700008));
                hasError = true;
            }

            if (hasError) {
                Snackbar.make(parent_layout, getResources().getString(R.string.Exc_700007), Snackbar.LENGTH_SHORT).show();
            } else {
                if (checkInternetConnection()) {
                    clearError();
                    saveUserInfo();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        photo.setOnClickListener(view -> imagePicker());

        photo.setOnLongClickListener(view -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            LayoutInflater inflater = getLayoutInflater();
            View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
            builder.setView(listViewDialog);
            TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
            ListView listView = listViewDialog.findViewById(R.id.dialogList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_expandable_list_item,
                    getResources().getStringArray(R.array.photo_menu_lc));
            listView.setAdapter(adapter);
            final AlertDialog dialog = builder.create();
            dialog.show();
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                String selectedListItem = ((TextView) view1).getText().toString();
                dialog.dismiss();
                if (selectedListItem.equals("انتخاب تصویر")) {
                    imagePicker();
                } else if (selectedListItem.equals("حذف تصویر")) {
                    photo.setImageResource(R.drawable.default_profile);
                }
            });
            return true;
        });

        birthDate.setOnClickListener(view -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    EnrolActivity.this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            datePickerDialog.show(getFragmentManager(), "Datepickerdialog");

        });

    }


    //check birth date
    private boolean invalidBirthDate() {
        Date now = DateUtil.toDate( DateUtil.getCurrentDate() );
        Date birth = DateUtil.toDate( birthDate.getText().toString().trim() );
        return birth.after(now);
    }

    private void setUserInfo() {
        final Bitmap photoBitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        Drawable myDrawable = getResources().getDrawable(R.drawable.default_profile);
        final Bitmap defaultProfileBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        if (!photoBitmap.sameAs(defaultProfileBitmap)) {
            userInfo.setPhoto(BitmapUtils.convertBitmapToByteArray(photoBitmap));
        } else {
            userInfo.setPhoto(null);
        }
        userInfo.setIdCode(idCode.getText().toString().trim());
        userInfo.setFirstName(name.getText().toString().trim());
        userInfo.setLastName(lastName.getText().toString().trim());
        userInfo.setBirthDate(birthDate.getText().toString().trim());
    }

    private void saveUserInfo() {
        //save userInfo into db and return id to save in shared preferences

        //save user info as shared preferences
        //savePreferences("user_id", id );
        //savePreferences("user_photo", userInfo.getPhoto());
        /*saveProfileImage(photo);
        savePreferences("user_id_code", userInfo.getIdCode());
        savePreferences("user_first_name", userInfo.getFirstName());
        savePreferences("user_last_name", userInfo.getLastName());
        savePreferences("user_birth_date", userInfo.getBirthDate());*/

        //get user id
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );
        int id = sharedPreferences.getInt("user_id", 0);

        SubmitProfileRequest request = new SubmitProfileRequest();
        request.setUserId( id );
        request.setName( name.getText().toString().trim() );
        request.setSurname( lastName.getText().toString().trim() );
        request.setBirthDate( birthDate.getText().toString().trim() );
        request.setNationalCode( idCode.getText().toString().trim() );
        request.setImage( convertProfileImage( photo ));

        TaskCallBack<Object> submitProfileResponse = result -> {
            SubmitProfileResponse ress = JsonCodec.toObject((Map) result, SubmitProfileResponse.class);

            boolean submit = ress.getSuccessful();
            if( submit ) {
                launchProfileView();
                Toast.makeText( context, getResources().getString(R.string.welcome), Toast.LENGTH_SHORT).show();
            }
            else {
                //toast error
                Toast.makeText(context, "result " + ress.getSuccessful(), Toast.LENGTH_SHORT).show();
            }
        };
        AsyncTask<Object, Void, WsResult> list = new ListHttp(submitProfileResponse, this, null, ServiceNames.SUBMIT_PROFILE, false);
        list.execute(request);

    }

    private void launchProfileView() {
        Intent intent = new Intent(EnrolActivity.this, UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearError() {
        idCodeLayout.setError(null);
        nameLayout.setError(null);
        lastNameLayout.setError(null);
        birthDateLayout.setError(null);
    }


    @Override
    public void onDateSet(com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int moth = monthOfYear + 1;
        //check date validation
        birthDate.setText(year + "/" + moth + "/" + dayOfMonth);
    }


    static class GenericTextWatcher implements TextWatcher {

        private View view;
        private TextInputLayout layout;

        GenericTextWatcher(View view, TextInputLayout layout) {
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


    private boolean checkInternetConnection() {
        //if connected return true
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable() || !info.isConnected()) {
            return false;
        }
        return true;
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    //convert image into string for saving into DB
    public String convertProfileImage(CircularImageView view) {
        /*photo.buildDrawingCache();
        Bitmap bitmap = photo.getDrawingCache();*/
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        String img_str = Base64.encodeToString(image, Base64.NO_WRAP);
        return img_str;
        //decode string to image
        /*String base=img_str;
        byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
        photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes,0, imageAsBytes.length) );*/
        //save in sharedpreferences
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_photo", img_str);
        editor.commit();*/
    }





    /*
    * handle photo picker
    *
    * */


    private void imagePicker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View listViewDialog = inflater.inflate(R.layout.alertdialog_with_list, null);
        builder.setView(listViewDialog);
        TextView listTitle = listViewDialog.findViewById(R.id.listAlertDialogTitle);
        ListView listView = listViewDialog.findViewById(R.id.dialogList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_expandable_list_item,
                getResources().getStringArray(R.array.photo_menu));
        listView.setAdapter(adapter);
        final AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedListItem = ((TextView) view).getText().toString();
                dialog.dismiss();
                if (selectedListItem.equals("دوربین")) {
                    ClickImageFromCamera() ;
                } else if (selectedListItem.equals("گالری")) {
                    GetImageFromGallery();
                }
            }
        });
    }


    public void ClickImageFromCamera() {

        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);

        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        CamIntent.putExtra("return-data", true);

        startActivityForResult(CamIntent, 0);

    }

    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {

            ImageCropFunction();

        }
        else if (requestCode == 2) {

            if (data != null) {

                uri = data.getData();

                ImageCropFunction();

            }
        }
        else if (requestCode == 1) {

            if (data != null) {

                Bundle bundle = data.getExtras();

                Bitmap bitmap = bundle.getParcelable("data");

                photo.setImageBitmap(bitmap);

            }
        }
    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            /*CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);*/
            CropIntent.putExtra("aspectX", 4);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }
    //Image Crop Code End Here

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(EnrolActivity.this,
                Manifest.permission.CAMERA))
        {

            Toast.makeText(EnrolActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(EnrolActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(EnrolActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(EnrolActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent( EnrolActivity.this, Main.class);
        startActivity( intent );
        finish();
    }


}
