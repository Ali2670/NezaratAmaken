package com.ibm.hamsafar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.ibm.hamsafar.R;

import java.io.OutputStream;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by maryam on 1/2/2019.
 */
public class ShowImageActivity extends Activity{


    private Context context = this;
    private ImageView share = null;
    private ImageView back = null;
    private ImageView photo = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.show_image);

        share = findViewById(R.id.show_image_share );
        back = findViewById(R.id.show_image_back );
        photo = findViewById(R.id.show_image_photo);

        String photo_str = getIntent().getStringExtra("photo");
        byte[] imageAsBytes = Base64.decode(photo_str.getBytes(), Base64.DEFAULT);
        photo.setImageBitmap(BitmapUtils.convertCompressedByteArrayToBitmap(imageAsBytes));
        //photo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length) );


        back.setOnClickListener(view -> onBackPressed());

        share.setOnClickListener(view -> {
            Bitmap icon = ((BitmapDrawable)photo.getDrawable()).getBitmap();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "title");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);


            OutputStream outstream;
            try {
                outstream = getContentResolver().openOutputStream(uri);
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));
        });
    }
}
