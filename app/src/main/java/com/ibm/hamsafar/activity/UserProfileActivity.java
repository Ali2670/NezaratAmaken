package com.ibm.hamsafar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.object.UserInfo;
import com.mikhaellopez.circularimageview.CircularImageView;

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

     public void onCreate(Bundle bundle) {
         super.onCreate( bundle );
         setContentView( R.layout.user_profile );

         sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

         fullName = findViewById(R.id.view_full_name);
         photo = findViewById(R.id.view_profile_photo);
         toolbarBack = findViewById(R.id.toolbar_back);
         toolbarTitle = findViewById(R.id.toolbar_text);

         if( getIntent().hasExtra("user")) {
             userInfo = (UserInfo) getIntent().getSerializableExtra("user");
         }

         toolbarTitle.setText(getResources().getString(R.string.view_title));

         toolbarBack.setOnClickListener(view -> onBackPressed());


         /*Toolbar toolbar = findViewById(R.id.view_toolbar);
         setSupportActionBar(toolbar);

         if (getSupportActionBar() != null) {
             getSupportActionBar().setTitle("");
             getSupportActionBar().setHomeButtonEnabled(true);
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         }*/

         fullName.setText( userInfo.getFirstName() + " " + userInfo.getLastName());

         if( userInfo.getPhoto() != null ) {
             //photo.setImageBitmap(BitmapUtils.convertCompressedByteArrayToBitmap(userInfo.getPhoto()));
             getImageFromCache();
         }


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

}
