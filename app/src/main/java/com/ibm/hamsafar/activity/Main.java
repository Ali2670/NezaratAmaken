package com.ibm.hamsafar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.PosterAdapter;
import com.ibm.hamsafar.utils.Constants;
import com.ibm.hamsafar.utils.Tools;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener/*, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener*/ {

    private Context context = this;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    TextView menuText;
    ImageView menuImage;
    private Menu menu;
    private SwipeRefreshLayout mainRefreshLayout = null;
    private SliderLayout sliderLayout = null;
    private NavigationView navigationView = null;
    private FloatingActionButton addTrip = null;
    private ImageButton toll = null;
    private ImageButton no_exit = null;
    private ImageButton tourism_info = null;
    private ImageButton police_10 = null;
    private ImageButton hotel_info = null;
    private ImageButton museum_info = null;
    private SharedPreferences sharedPreferences;

    //recycler view
    private int[] posters;
    private RecyclerView first;
    private RecyclerView second;
    private LinearLayoutManager linearLayoutManager;
    private PosterAdapter adapter;


    private Intent intent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        mainRefreshLayout = findViewById(R.id.main_swipe_refresh_layout);
        navigationView = findViewById(R.id.nav_view);
        first = findViewById(R.id.firstRecyclerView);
        second = findViewById(R.id.secondRecyclerView);
        addTrip = findViewById(R.id.home_add_trip);
        toll = findViewById(R.id.main_toll_btn);
        no_exit = findViewById(R.id.main_no_exit_btn);
        tourism_info = findViewById(R.id.main_tourism_info_btn);
        police_10 = findViewById(R.id.main_police_10_btn);
        hotel_info = findViewById(R.id.main_hotel_info_btn);
        museum_info = findViewById(R.id.main_museum_info_btn);

        mainRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimaryLight),
                getResources().getColor(R.color.colorPrimaryVDark));
        mainRefreshLayout.setOnRefreshListener(() -> mainRefreshLayout.setRefreshing(false));

        /*
        * fill recycler view
        * */

        posters = new int[6];
        posters[0] = R.drawable.slider_image_1;
        posters[1] = R.drawable.slider_image_2;
        posters[2] = R.drawable.slider_image_3;
        posters[3] = R.drawable.slider_image_4;
        posters[4] = R.drawable.slider_image_5;
        posters[5] = R.drawable.slider_image_6;

        linearLayoutManager = new LinearLayoutManager(Main.this, LinearLayoutManager.HORIZONTAL, true);
        first.setLayoutManager(linearLayoutManager);
        adapter = new PosterAdapter(this, posters);
        first.setAdapter(adapter);

        linearLayoutManager = new LinearLayoutManager(Main.this, LinearLayoutManager.HORIZONTAL, true);
        second.setLayoutManager(linearLayoutManager);
        adapter = new PosterAdapter(this, posters);
        second.setAdapter(adapter);


        toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setSupportActionBar(toolbar);
        Tools.customizeToolbar(toolbar);


        //slider
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.THIN_WORM);
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        setSliderViews();

        addTrip.setOnClickListener(view -> {
            Intent intent = new Intent( Main.this, TripActivity.class );
            startActivity( intent );
        });

        toll.setOnClickListener(view -> {
            intent = new Intent(Main.this, WebPageActivity.class);
            intent.putExtra("url", "https://tollpayment.sadadpsp.ir");
            intent.putExtra("title", getResources().getString(R.string.nav_menu_item_toll));
            startActivity( intent );
        });

        no_exit.setOnClickListener(view -> {
            intent = new Intent(Main.this, WebPageActivity.class);
            intent.putExtra("url", "http://exitban.ssaa.ir");
            intent.putExtra("title", getResources().getString(R.string.nav_menu_item_no_exit));
            startActivity( intent );
        });

        tourism_info.setOnClickListener(view -> {
            intent = new Intent( Main.this, PlaceInfoActivity.class );
            intent.putExtra("type", "tourism");
            startActivity( intent );
        });

        police_10.setOnClickListener(view -> {
            intent = new Intent( Main.this, PlaceInfoActivity.class );
            intent.putExtra("type", "police");
            startActivity( intent );
        });

        hotel_info.setOnClickListener(view -> {
            intent = new Intent( Main.this, PlaceInfoActivity.class );
            intent.putExtra("type", "hotel");
            startActivity( intent );
        });

        museum_info.setOnClickListener(view -> {
            intent = new Intent( Main.this, PlaceInfoActivity.class );
            intent.putExtra("type", "museum");
            startActivity( intent );
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                SetMenu();

            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        toolbar.setNavigationOnClickListener(v -> {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setItemIconTintList(null);
        hideItem();
        menuText = navView.findViewById(R.id.loggedInUser);
        menuImage = navView.findViewById(R.id.userImage);
        menuImage.setOnClickListener(view -> {
            String user = Tools.getDefaults(Constants.UserName, Main.this);
        });

    }

    private void SetMenu() {
        if (Tools.UserHasLoggedIn()) {
            if (menu != null && menu.getItem(0) != null) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.profile));
            }


//            Tools.LoadImageProfile(menuImage, Tools.getDefaults(Constants.UserImage));
            menuText.setText(Tools.getDefaults(Constants.UserName));

        } else {
      /*      if (menu != null && menu.getItem(0) != null)
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.login));

            menuImage.setImageResource(R.drawable.login);
            menuText.setText(Tools.getStringByName("LoginPlease"));*/

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        } else {
            if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(context, Tools.getStringByName("DoubleBack"),
                        Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SetMenu();
        String userId = Tools.getDefaults(Constants.UserId);

        if (userId != null && !userId.equals("")) {

            String pusheId = Tools.getDefaults(Constants.PusheId);
            if (pusheId == null || pusheId.trim().equals("")) {
            }
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        this.menu = menu;
        SetMenu();

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.main_nav_show_trips:
                startActivity( new Intent(Main.this, TripListActivity.class));
                break;
            case R.id.main_nav_show_checklists:
                startActivity( new Intent(Main.this, ChecklistListActivity.class));
                break;
            case R.id.main_nav_trip:
                startActivity( new Intent(Main.this, TripActivity.class));
                break;
            case R.id.main_nav_checklist:
                startActivity( new Intent(Main.this, CheckListItemAddActivity.class));
                break;
            case R.id.main_nav_login:
                if( sharedPreferences.contains("user_id") && sharedPreferences.getInt("user_id", 0) != 0) {
                    startActivity(new Intent(Main.this, EnrolActivity.class));
                } else {
                    startActivity(new Intent(Main.this, LoginActivity.class));
                }
                break;
            case R.id.main_nav_edit:
                startActivity(new Intent(Main.this, EditProfileActivity.class));
                break;
            case R.id.main_nav_friends:

                break;
            case R.id.main_nav_map:

                break;
            case R.id.main_nav_notification:

                break;
            case R.id.main_nav_search:

                break;
            case R.id.main_nav_logout:
                savePreferences("user_id", 0);
                hideItem();
                Toast.makeText(context, "خروج از حساب کاربری", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_nav_toll:
                intent = new Intent(Main.this, WebPageActivity.class);
                intent.putExtra("url", "https://tollpayment.sadadpsp.ir");
                intent.putExtra("title", getResources().getString(R.string.nav_menu_item_toll));
                startActivity( intent );
                break;
            case R.id.main_nav_no_exit:
                intent = new Intent(Main.this, WebPageActivity.class);
                intent.putExtra("url", "http://exitban.ssaa.ir");
                intent.putExtra("title", getResources().getString(R.string.nav_menu_item_no_exit));
                startActivity( intent );
                break;
            case R.id.main_nav_share:
                ApplicationInfo app = getApplicationContext().getApplicationInfo();
                String filePath = app.sourceDir;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                startActivity(Intent.createChooser(sharingIntent, "share app via"));
                break;
            case R.id.main_nav_support:

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(Gravity.RIGHT);
        return true;
    }
    //----------------------------------------------------------------------------------------------

    private void setSliderViews() {

        for (int i = 0; i <= 5; i++) {

            SliderView sliderView = new SliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.slider_image_1);
                    sliderView.setDescription("Scholoss Schwerin-Germany");
                    //sliderView.setImageUrl("https://images.pexels.com/photos/547114/pexels-photo-547114.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.slider_image_2);
                    sliderView.setDescription("Loire Valley-France");
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.slider_image_3);
                    sliderView.setDescription("Grotta Palazzese-Italy");
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.slider_image_4);
                    sliderView.setDescription("The Colosseum-Rom");
                    break;
                case 4:
                    sliderView.setImageDrawable(R.drawable.slider_image_5);
                    sliderView.setDescription("Amir Chakhmaq-Yazd");
                    break;
                case 5:
                    sliderView.setImageDrawable(R.drawable.slider_image_6);
                    sliderView.setDescription("Jame mosque-Yazd");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            //sliderView.setDescription("setDescription " + (i + 1));
            final int finalI = i;
            sliderView.setOnSliderClickListener(sliderView1 -> Toast.makeText(context, "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show());

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }

    private void hideItem() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Menu nav_Menu = navigationView.getMenu();
        //hide login topic
        if( sharedPreferences.contains("user_id") ) {
            if( sharedPreferences.getInt("user_id", 0) != 0 ) {
                nav_Menu.findItem(R.id.main_nav_login).setVisible(false);
                nav_Menu.findItem(R.id.main_nav_edit).setVisible(true);
                nav_Menu.findItem(R.id.main_nav_logout).setVisible(true);
                nav_Menu.findItem(R.id.main_nav_friends).setVisible(true);
                //nav_Menu.findItem(R.id.group1).setVisible(true);
            }
            else {
                nav_Menu.findItem(R.id.main_nav_login).setVisible(true);
                nav_Menu.findItem(R.id.main_nav_edit).setVisible(false);
                nav_Menu.findItem(R.id.main_nav_logout).setVisible(false);
                nav_Menu.findItem(R.id.main_nav_friends).setVisible(false);
                //nav_Menu.findItem(R.id.group1).setVisible(false);
            }
        }
        else {
            nav_Menu.findItem(R.id.main_nav_login).setVisible(true);
            nav_Menu.findItem(R.id.main_nav_edit).setVisible(false);
            nav_Menu.findItem(R.id.main_nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.main_nav_friends).setVisible(false);
            //nav_Menu.findItem(R.id.group1).setVisible(false);
        }
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreferences(String key, Integer value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


}
