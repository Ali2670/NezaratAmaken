package com.ibm.hamsafar.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.asyncTask.ListHttp;
import com.ibm.hamsafar.asyncTask.TaskCallBack;
import com.ibm.hamsafar.object.UserInfo;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import hamsafar.ws.common.ProfileDto;
import hamsafar.ws.model.JsonCodec;
import hamsafar.ws.request.GetProfileRequest;
import hamsafar.ws.response.GetProfileResponse;
import hamsafar.ws.util.service.ServiceNames;
import ibm.ws.WsResult;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProfileActivity extends AppCompatActivity {

    private Context context = this;
    private UserInfo userInfo = new UserInfo();
    private TextView fullName = null;
    private CircularImageView photo = null;
    private SharedPreferences sharedPreferences;
    private Button toolbarBack = null;
    private TextView toolbarTitle = null;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

     @SuppressLint("SetTextI18n")
     public void onCreate(Bundle bundle) {
         super.onCreate( bundle );
         setContentView( R.layout.user_profile );

         sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

         fullName = findViewById(R.id.view_full_name);
         photo = findViewById(R.id.view_profile_photo);
         toolbarBack = findViewById(R.id.toolbar_back);
         toolbarTitle = findViewById(R.id.toolbar_text);

         getUserInfo();


         /*if( getIntent().hasExtra("user")) {
             userInfo = (UserInfo) getIntent().getSerializableExtra("user");
         }*/

         toolbarTitle.setText(getResources().getString(R.string.view_title));

         toolbarBack.setOnClickListener(view -> onBackPressed());

         //download image and show to user
         photo.setOnClickListener(view -> {
             Intent intent = new Intent( UserProfileActivity.this, ShowImageActivity.class);
             intent.putExtra( "photo", convertProfileImage(photo));
             startActivity( intent );
         });


         /*Toolbar toolbar = findViewById(R.id.view_toolbar);
         setSupportActionBar(toolbar);

         if (getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
             getSupportActionBar().setHomeButtonEnabled(true);
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         }*/

         /*fullName.setText( userInfo.getFirstName() + " " + userInfo.getLastName());

         if( userInfo.getPhoto() != null ) {
             //photo.setImageBitmap(BitmapUtils.convertCompressedByteArrayToBitmap(userInfo.getPhoto()));
             getImageFromCache();
         }*/


     }

    private void getImageFromCache() {
        String img_str = sharedPreferences.getString("user_photo", "");
        if (!img_str.equals("")){
            //decode string to image
            String base=img_str;
            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length) );
        }
    }

     public void onBackPressed() {
        Intent back = new Intent( UserProfileActivity.this, Main.class );
        startActivity( back );
        finish();
     }

     private void getUserInfo() {
         GetProfileRequest request = new GetProfileRequest();
         request.setUserId( sharedPreferences.getInt("user_id", 0));

         TaskCallBack<Object> getProfileRequest = result -> {
             GetProfileResponse ress = JsonCodec.toObject((Map) result, GetProfileResponse.class);
             ProfileDto profileDto = ress.getProfileDto();
             userInfo.setFirstName( profileDto.getName() );
             userInfo.setLastName( profileDto.getSurname() );
             userInfo.setBirthDate( profileDto.getBirthDate() );

             //get photo and convert to bitmap
             byte[] imageAsBytes = Base64.decode(profileDto.getImage().getBytes(), Base64.DEFAULT);
             //photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes,0, imageAsBytes.length) );
             userInfo.setPhoto( imageAsBytes);
             setInfo( userInfo );
         };
         AsyncTask<Object, Void, WsResult> list = new ListHttp(getProfileRequest, this, null, ServiceNames.GET_USER_PROFILE, false);
         list.execute(request);
     }

     //show user info
    private void setInfo( UserInfo info ) {
        fullName.setText( info.getFirstName() + " " + info.getLastName());
        if( info.getPhoto() != null ) {
            photo.setImageBitmap(BitmapUtils.convertCompressedByteArrayToBitmap(info.getPhoto()));
        }
    }

    //convert image into string for saving into DB
    public String convertProfileImage(CircularImageView view) {
        Bitmap bitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        String img_str = Base64.encodeToString(image, Base64.NO_WRAP);
        return img_str;
    }

}
