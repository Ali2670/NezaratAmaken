package com.ibm.hamsafar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ibm.hamsafar.Application.AppStart;
import com.ibm.hamsafar.R;
import com.ibm.hamsafar.services.PcService;
import com.ibm.hamsafar.utils.layouts.ZoomableImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Hamed
 */
public class Tools {
    public static Integer SERVER_ID = 0;
    public static String Text = "hamed-heydari.Com";
    public static String URL_MEDIA[] = new String[3];
    //        public static String URL_MEDIA = "http://89.32.249.190:8001/";
    public static String version = "1.1.3";
    //    public static String URL_PREF_WS = "http://5.160.71.43:9001/ws/msgws/";
    public static String URL_PREF_WS = "http://5.160.0.0:9001/ws/msgws/";
    public static String URL_APK[] = new String[3];
    //    public static String IMAGE_URL = "http://test.com/components/com_contushdvideoshare/videos/";
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static Integer firstListSite = 18;
    public static HashMap<String, Long> downloadManger = new HashMap<>();
    public static DisplayImageOptions optionsBigImage = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .showImageOnLoading(R.drawable.loading) // resource or drawable
            .showImageOnFail(R.drawable.notfound) // resource or drawable
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .build();
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .displayer(new RoundedBitmapDisplayer(11))
            .showImageOnLoading(R.drawable.loading) // resource or drawable
            .showImageOnFail(R.drawable.notfound) // resource or drawable

            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
    public static DisplayImageOptions optionsRound = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .displayer(new RoundedBitmapDisplayer(90))
            .showImageOnLoading(R.drawable.loading) // resource or drawable
            .showImageOnFail(R.drawable.notfound) // resource or drawable

            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
    public static DisplayImageOptions optionsRoundProfile = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .displayer(new RoundedBitmapDisplayer(90))
            .showImageOnLoading(R.drawable.loading) // resource or drawable
            .showImageOnFail(R.drawable.profile) // resource or drawable

            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
    public static DisplayImageOptions optionsBg = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .displayer(new SimpleBitmapDisplayer())
            .showImageOnLoading(R.drawable.loading) // resource or drawable
            .showImageOnFail(R.drawable.notfound) // resource or drawable

            .imageScaleType(ImageScaleType.EXACTLY)
            .build();
    public static Typeface tf;
    static AlertDialog alertDialogAndroid;

    public Tools() {

    }

    private static byte[] KeyCreator(byte[] deviceId) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(deviceId);
            kgen.init(128, sr);
            SecretKey skey = kgen.generateKey();
            byte[] key = skey.getEncoded();
            return key;
        } catch (Exception var6) {
            return "Hamed0989192507810".getBytes();
        }
    }

    public static String ChangeNumbers(Object obj) {
        if (obj == null) {
            return null;
        }
        String str = obj.toString();
        String finalStr = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String selected = c + "";
            if ((c >= '0' && c <= '9')) {
                switch (Character.getNumericValue(c)) {
                    case 0:
                        selected = getStringByName("sefr");
                        break;
                    case 1:
                        selected = getStringByName("yek");
                        break;
                    case 2:
                        selected = getStringByName("dou");
                        break;
                    case 3:
                        selected = getStringByName("se");
                        break;
                    case 4:
                        selected = getStringByName("chahar");
                        break;
                    case 5:
                        selected = getStringByName("panj");
                        break;
                    case 6:
                        selected = getStringByName("shesh");
                        break;
                    case 7:
                        selected = getStringByName("haft");
                        break;
                    case 8:
                        selected = getStringByName("hasht");
                        break;
                    case 9:
                        selected = getStringByName("noh");
                        break;

                }
            }
            finalStr = finalStr + selected;
        }
        return finalStr;
    }


    public static final String MD5Rec(String str) {
        str = str + Text + "9192507810";
        String MD5 = "MD5";

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(str.getBytes());
            byte[] messageDigest = e.digest();
            StringBuilder hexString = new StringBuilder();
            byte[] var5 = messageDigest;
            int var6 = messageDigest.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                byte aMessageDigest = var5[var7];

                String h;
                for (h = Integer.toHexString(255 & aMessageDigest); h.length() < 2; h = "0" + h) {
                    ;
                }

                hexString.append(h);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException var10) {
            var10.printStackTrace();
            return "";
        }
    }

    public static final String MD5Send(String str) {
        str = str + "9192507810" + Text;
        String MD5 = "MD5";

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(str.getBytes());
            byte[] messageDigest = e.digest();
            StringBuilder hexString = new StringBuilder();
            byte[] var5 = messageDigest;
            int var6 = messageDigest.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                byte aMessageDigest = var5[var7];

                String h;
                for (h = Integer.toHexString(255 & aMessageDigest); h.length() < 2; h = "0" + h) {
                    ;
                }

                hexString.append(h);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException var10) {
            var10.printStackTrace();
            return "";
        }
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 100;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }

    public static boolean isNetworkAvailable(Context context) {
        try {

            ConnectivityManager CN = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return ((CN).getActiveNetworkInfo()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setDefaults(String key, String value, Context context) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

//            value=    CryptoUtil.encrypt(value, "9192507810@Hamed");

            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDefaults(String key, String value) {
        setDefaults(key, value, AppStart.getContext());
    }

    public static String getDefaults(String key, Context context) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String value = preferences.getString(key, "");
//            value=    CryptoUtil.decrypt(value, "9192507810@Hamed");
            return value;

        } catch (Exception e) {
            return "";
        }
    }

    public static String getDefaults(String key) {
        return getDefaults(key, AppStart.getContext());
    }

    public static void StatusChangeColor(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void StatusChangeColor(Context context, int color) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.setStatusBarColor(color);
        }
    }

    public static void LoadImage(final ImageView iv, String url, Boolean round) {
//        Picasso.with(this)
//                .load(url)
////                .placeholder(R.drawable.ic_placeholder)   // optional
////                .error(R.drawable.ic_error_fallback)      // optional
////                .resize(250, 200)                        // optional
////                .rotate(90)                             // optional
//                .into(iv);

        if (url == null || url.toLowerCase().trim().equals("null_thumb")) {
            iv.setImageResource(R.drawable.notfound);
        } else {
            url = url.replaceAll("\\\\", "/");
            url = Tools.URL_MEDIA[SERVER_ID] + url;
            if (round) {
                imageLoader.displayImage(url, iv, optionsRound);

            } else {
                imageLoader.displayImage(url, iv, options);
            }
        }

    }

    public static void LoadImageProfile(final ImageView iv, String url) {

        if (url == null || url.toLowerCase().trim().equals("null_thumb")) {
            iv.setImageResource(R.drawable.profile);
        } else {
            url = url.replaceAll("\\\\", "/");
            url = Tools.URL_MEDIA[SERVER_ID] + url;

            imageLoader.displayImage(url, iv, optionsRoundProfile);

        }

    }

    public static void LoadImageBg(final ImageView iv, String url) {
        if (url == null || url.toLowerCase().trim().equals("null_thumb")) {
            iv.setImageResource(R.drawable.notfound);
        } else {
            url = url.replaceAll("\\\\", "/");
            url = Tools.URL_MEDIA[SERVER_ID] + url;
            imageLoader.displayImage(url, iv, optionsBg);

        }

    }

    public static void LoadImage(final ZoomableImageView iv, String url) {

        if (url == null || url.toLowerCase().trim().equals("null_thumb")) {
            iv.setImageResource(R.drawable.notfound);
        } else {
            url = url.replaceAll("\\\\", "/");
            url = Tools.URL_MEDIA[SERVER_ID] + url;

            imageLoader.displayImage(url, iv);


        }

    }

    public static void RemoveImageCache(String imageUri) {
        File imageFile = imageLoader.getDiskCache().get(imageUri);
        if (imageFile != null && imageFile.exists()) {
            imageFile.delete();
        }
        MemoryCacheUtils.removeFromCache(imageUri, imageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache(imageUri, imageLoader.getDiskCache());
    }

    private static byte[] KeyCreator() {
        byte abyte0[];
        try {
            abyte0 = GetDeviceId(AppStart.getContext()).getBytes("UTF-8");
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            SecureRandom securerandom = SecureRandom.getInstance("SHA1PRNG");
            securerandom.setSeed(abyte0);
            keygenerator.init(128, securerandom);
            abyte0 = keygenerator.generateKey().getEncoded();
        } catch (Exception exception) {
            return "Hamed0989192507810".getBytes();
        }
        return abyte0;
    }

    public static byte[] getScaledImage(byte[] originalImage, int newWidth, int newHeight) {
        // PNG has not losses, it just ignores this field when compressing
        final int COMPRESS_QUALITY = 0;

        // Get the bitmap from byte array since, the bitmap has the the resize function
        Bitmap bitmapImage = (BitmapFactory.decodeByteArray(originalImage, 0, originalImage.length));
        if (newHeight == 0) {
            newHeight = newWidth * bitmapImage.getHeight() / bitmapImage.getWidth();
        }

        if (newWidth == 0) {
            newWidth = newHeight * bitmapImage.getWidth() / bitmapImage.getHeight();
        }
        // New bitmap with the correct size, may not return a null object
        Bitmap mutableBitmapImage = Bitmap.createScaledBitmap(bitmapImage, newWidth, newHeight, false);

        // Get the byte array from tbe bitmap to be returned
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mutableBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        if (mutableBitmapImage != bitmapImage) {
            mutableBitmapImage.recycle();
        } // else they are the same, just recycle once

        bitmapImage.recycle();
        return outputStream.toByteArray();
    }

    public static String GetDeviceId(Context context) {
        String s = android.provider.Settings.Secure.getString(context.getContentResolver(), "android_id");

        if (s == null) {
            s = "+989192507810";
        }
        return s;
    }

    public static byte[] UnCompressByte(byte[] data) throws IOException {
        if (data != null && data.length != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            GZIPInputStream is = new GZIPInputStream(bais);
            byte[] tmp = new byte[256];

            while (true) {
                int content = is.read(tmp);
                if (content < 0) {
                    is.close();
                    byte[] content1 = buffer.toByteArray();
                    return content1;
                }

                buffer.write(tmp, 0, content);
            }
        } else {
            return null;
        }
    }

    public static byte[] CompressToByte(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream os = new GZIPOutputStream(baos);
        os.write(data, 0, data.length);
        os.close();
        byte[] result = baos.toByteArray();
        return result;
    }

    private static byte[] Encryptor(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] Decryptor(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    /*public static String CallWs(String... params) {
        HttpResponse response;
        InputStream iss = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(Tools.URL_PREF_WS + params[1]);
            httppost.addHeader("Accept", "text/plain");
            httppost.addHeader("Content-Type", "text/plain");

            StringEntity myParam = new StringEntity(CreateJson(params[0]), "UTF-8");
            httppost.setEntity(myParam);
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            iss = entity.getContent();


        } catch (Exception ex) {
            System.out.println("");

        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(iss, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            iss.close();

            result = sb.toString();
            return result;
        } catch (Exception e) {


        }

        return "Executed";
    }*/

    public static String getStringByName(String name) {
        try {

            Context context = AppStart.getContext();
            Resources resource = context.getResources();
            String str = resource.getString(resource.getIdentifier(name, "string", context.getPackageName()));
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    public static byte[] BitMapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
    }

    public static String MacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            try {
                return GetDeviceId(AppStart.getContext());
            } catch (Exception e) {

                return "+989192507810";
            }
        }
        return "09192507810 : Heydari";
    }

    public static String CheckString(Object likes) {
        if (likes == null)
            return "0";
        else
            return likes.toString();
    }

    public static boolean UserHasLoggedIn() {
        if (
                Tools.getDefaults(Constants.SessionId, AppStart.getContext()) != null
                        && Tools.getDefaults(Constants.SessionId, AppStart.getContext()).trim().length() > 0
                        && Tools.getDefaults(Constants.UserName, AppStart.getContext()) != null
                        && Tools.getDefaults(Constants.UserName, AppStart.getContext()).trim().length() > 0
                ) {
            return true;
        } else {
            return false;
        }

    }

    public static void SendPm(Object visitObject) {
        try {
            String str = new Gson().toJson(visitObject);
            if (PcService.queue != null) {
                PcService.queue.addFirst(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShowDialog(Context context, String title, String text) {
        ShowDialog(context, title, text, true, false);
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void ShowDialog(Context context, String title, String text, Boolean finish) {
        ShowDialog(context, title, text, !finish, finish);
    }

    public static void ShowDialog(final Context context, String title, String text, Boolean cancle, final Boolean finish) {
        if (context != null) {
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            View mView = layoutInflaterAndroid.inflate(R.layout.dialog_box, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme)
//                .setTitle(title)
//                .setMessage("")
                    .setView(mView)
                    .setCancelable(cancle);
               /* .setPositiveButton(getStringByName("ok"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
            Button bt = mView.findViewById(R.id.signUp);
            TextView message = mView.findViewById(R.id.title);
            TextView close = mView.findViewById(R.id.dialogClose);
            final AlertDialog d;

            try {

                alertDialogAndroid = builder.create();
                d = builder.show();
                if (alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                    alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(context.getResources().getColor(R.color.white));
                }
                if (alertDialogAndroid.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                    alertDialogAndroid.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(context.getResources().getColor(R.color.white));
                }
                message.setText(text);
                if (text != null &&
                        (text.equals(getStringByName("Exc.100009")) ||
                                text.equals(getStringByName("Exc.100008")) ||
                                text.equals(getStringByName("Exc.300014")) ||
                                text.equals(getStringByName("Exc.100007"))
                        )) {
                    bt.setVisibility(View.VISIBLE);
                    final Context c = context;
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(c, SignupActivity.class);
//                            c.startActivity(intent);
                            d.dismiss();
                        }
                    });
                } else {
                    bt.setVisibility(View.GONE);
                }
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        if (finish) {
                            ((Activity) context).finish();
                        }
                    }
                });
            } catch (Exception e) {

            }
        }
    }


    public static void customizeToolbar(Toolbar toolbar) {
        // Save current title and subtitle
        Typeface tf = Tools.tf;
        ;
        final CharSequence originalTitle = toolbar.getTitle();
        final CharSequence originalSubtitle = toolbar.getSubtitle();

        // Temporarily modify title and subtitle to help detecting each
        toolbar.setTitle("title");
        toolbar.setSubtitle("subtitle");

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);

            if (view instanceof TextView) {
                TextView textView = (TextView) view;


                if (textView.getText().equals("title")) {
                    // Customize title's TextView


                    // Apply custom font using the Calligraphy library
                    textView.setTypeface(tf);

                } else if (textView.getText().equals("subtitle")) {
                    // Customize subtitle's TextView


                    // Apply custom font using the Calligraphy library
                    textView.setTypeface(tf);
                }
            }
        }

        // Restore title and subtitle
        toolbar.setTitle(originalTitle);
        toolbar.setSubtitle(originalSubtitle);
    }

    public static void AdsClick(Long id, Integer action, String url, Context context) {
//        Tools.SendPm(new Object(Constants.VISIT_TYPE_ADS_CLICK, id));

        if (action == 1) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    public static void customizeTabLayout(TabLayout tabLayout, Context context) {
        // Save current title and subtitle
        try {


            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                //noinspection ConstantConditions

                CharSequence txt = tabLayout.getTabAt(i).getText();
                TextView tabOne = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
                tabOne.setText(txt);
                tabOne.setTypeface(tf);
                tabOne.setTextColor(Color.BLACK);
                tabLayout.getTabAt(i).setCustomView(tabOne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
