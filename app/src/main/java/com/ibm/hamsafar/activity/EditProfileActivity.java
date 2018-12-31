package com.ibm.hamsafar.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.hamsafar.persianmaterialdatetimepicker.utils.PersianCalendar;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.UserInfo;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends Activity implements DatePickerDialog.OnDateSetListener {

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
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;
    private UserInfo userInfo = new UserInfo();
    SharedPreferences sharedPreferences;

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
        super.onCreate( bundle );
        setContentView(R.layout.enrol );

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        toolbarTitle.setText(getResources().getString(R.string.edit_title));

        toolbarBack.setOnClickListener(view -> onBackPressed());

        //load user data into fields
        loadData();

        clearError();

        idCodeLayout.getEditText().addTextChangedListener(
                new EditProfileActivity.GenericTextWatcher(idCodeLayout.getEditText(), idCodeLayout));
        nameLayout.getEditText().addTextChangedListener(
                new EditProfileActivity.GenericTextWatcher(nameLayout.getEditText(), nameLayout));
        lastNameLayout.getEditText().addTextChangedListener(
                new EditProfileActivity.GenericTextWatcher(lastNameLayout.getEditText(), lastNameLayout));
        birthDateLayout.getEditText().addTextChangedListener(
                new EditProfileActivity.GenericTextWatcher(birthDateLayout.getEditText(), birthDateLayout));


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if( userInfo.getFirstName().equals("") ) {
                    nameLayout.setError(getResources().getString(R.string.Exc_700003));
                    hasError = true;
                }

                //check last name
                if(userInfo.getLastName().equals("") ) {
                    lastNameLayout.setError(getResources().getString(R.string.Exc_700004));
                    hasError = true;
                }

                //check birth date
                if( userInfo.getBirthDate().equals("") ) {
                    birthDateLayout.setError(getResources().getString(R.string.Exc_700005));
                    hasError = true;
                }
                if( hasError ) {
                    Snackbar.make(view, getResources().getString(R.string.Exc_700007), Snackbar.LENGTH_SHORT).show();
                }
                else {
                    if( checkInternetConnection() ) {
                        clearError();
                        updateUserInfo();
                        Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                        intent.putExtra("user", userInfo);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedListItem = ((TextView) view).getText().toString();
                        dialog.dismiss();
                        if (selectedListItem.equals("انتخاب تصویر")) {
                            imagePicker();
                        }
                        else if (selectedListItem.equals("حذف تصویر")) {
                            photo.setImageResource(R.drawable.default_profile);
                        }
                    }
                });
                return true;
            }
        });

        birthDate.setOnClickListener(view -> {
            PersianCalendar persianCalendar = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    EditProfileActivity.this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            datePickerDialog.show(getFragmentManager(), "Datepickerdialog");

        });

    }

    private void setUserInfo() {
        final Bitmap photoBitmap = ((BitmapDrawable)photo.getDrawable()).getBitmap();
        Drawable myDrawable = getResources().getDrawable(R.drawable.default_profile);
        final Bitmap defaultProfileBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        if( !photoBitmap.sameAs(defaultProfileBitmap) ) {
            userInfo.setPhoto(BitmapUtils.convertBitmapToByteArray(photoBitmap));
        }
        else {
            userInfo.setPhoto( null );
        }
        userInfo.setIdCode( idCode.getText().toString().trim() );
        userInfo.setFirstName( name.getText().toString().trim() );
        userInfo.setLastName( lastName.getText().toString().trim() );
        userInfo.setBirthDate( birthDate.getText().toString().trim() );
    }

    private void updateUserInfo() {
        //update user info using its id

        //save user info as shared preferences
        //savePreferences("user_id", id );
        saveProfileImage( photo );
        savePreferences("user_id_code", userInfo.getIdCode());
        savePreferences("user_first_name", userInfo.getFirstName() );
        savePreferences("user_last_name", userInfo.getLastName() );
        savePreferences("user_birth_date", userInfo.getBirthDate() );

    }

    private void clearError(){
        idCodeLayout.setError(null);
        nameLayout.setError(null);
        lastNameLayout.setError(null);
        birthDateLayout.setError(null);
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
            if( s.toString().trim().length() > 0 )
                layout.setError( null );
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
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreferences( String key, byte[] value ) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String stValue = Base64.encodeToString(value, Base64.DEFAULT);
        editor.putString(key, stValue);
        editor.commit();
    }

    private void loadData() {
        getImageFromCache();
        idCode.setText( sharedPreferences.getString("user_id_code", "") );
        name.setText( sharedPreferences.getString("user_first_name", "") );
        lastName.setText( sharedPreferences.getString("user_last_name", "") );
        birthDate.setText( sharedPreferences.getString("user_birth_date", "") );
    }

    //convert string to byte array
    private void getImageFromCache() {
        String img_str = sharedPreferences.getString("user_photo", "");
        if (!img_str.equals("")){
            //decode string to image
            String base=img_str;
            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length) );
        }
    }


    public void saveProfileImage(View view){
        //code image to string
        photo.buildDrawingCache();
        Bitmap bitmap = photo.getDrawingCache();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image=stream.toByteArray();
        String img_str = Base64.encodeToString(image, 0);
        //decode string to image
        /*String base=img_str;
        byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
        photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes,0, imageAsBytes.length) );*/
        //save in sharedpreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_photo",img_str);
        editor.commit();
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
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,
                Manifest.permission.CAMERA))
        {

            Toast.makeText(context,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(context,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(context,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void onDateSet(com.hamsafar.persianmaterialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int moth = monthOfYear + 1;
        //check date validation
        birthDate.setText(year + "/" + moth + "/" + dayOfMonth);
    }



}

