package cn.edu.cqupt.nmid.headline.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.edu.cqupt.nmid.headline.R;
import cn.edu.cqupt.nmid.headline.api.weather.WeatherService;
import cn.edu.cqupt.nmid.headline.model.weather.Weather;
import cn.edu.cqupt.nmid.headline.model.weather.WeatherDatum;
import cn.edu.cqupt.nmid.headline.support.Constant;
import cn.edu.cqupt.nmid.headline.ui.adapter.PagerAdapter;
import cn.edu.cqupt.nmid.headline.ui.fragment.FeedFragment;
import cn.edu.cqupt.nmid.headline.ui.fragment.NavigationDawerFragment;
import cn.edu.cqupt.nmid.headline.ui.widget.SlidingTabLayout;
import cn.edu.cqupt.nmid.headline.utils.LogUtils;
import cn.edu.cqupt.nmid.headline.utils.NetworkUtils;
import cn.edu.cqupt.nmid.headline.utils.ScreenUtils;
import cn.edu.cqupt.nmid.headline.utils.ThemeUtils;
import cn.edu.cqupt.nmid.headline.utils.UIutils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 *
 */
public class HomeActivity extends ActionBarActivity implements NavigationDawerFragment.NavigationDrawerCallBack {

    private String TAG = LogUtils.makeLogTag(HomeActivity.class);
    private boolean isAnimate = true;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.slidingtab)
    SlidingTabLayout mTabLayout;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    MenuItem actionView;


    ActionBarDrawerToggle mDrawerToggle;

    PagerAdapter mPagerAdapter;

    ArrayList<FeedFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onRestart");
        ThemeUtils.setThemeFromDb(this);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        trySetupToolbarAndTab();


    }

    /**
     * Setup Toolbar and L-style Toggle
     */
    private void trySetupToolbarAndTab() {

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitleTextColor(Color.WHITE);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        trySyncWeather();
    }


    /**
     * Sync Weather data via rest service
     */
    private void trySyncWeather() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            new RestAdapter.Builder()
                    .setEndpoint(WeatherService.API_BAIDU_ENDPOINT)
                            //.setLogLevel(RestAdapter.LogLevel.FULL)
                    .build()
                    .create(WeatherService.class)
                    .getWeatherService(
                            WeatherService.API_BAIDU_WEATHER_AK,
                            WeatherService.API_BAIDU_WEATHER_LOCATION,
                            WeatherService.API_BAIDU_WEATHER_OUTPUT,
                            new Callback<Weather>() {
                                @Override
                                public void success(Weather weather, Response response) {

                                    WeatherDatum mWeatherData = weather.getResults().get(0).getWeather_data().get(0);

                                    ((TextView) actionView.getActionView().findViewById(R.id.weather_tempeture)).setText(
                                            mWeatherData.getWind() + " " + mWeatherData.getTemperature());

                                    Picasso.with(HomeActivity.this)
                                            .load(mWeatherData.getDayPictureUrl())
                                            .into(((ImageView) actionView.getActionView().findViewById(R.id.weather_img))
                                            );

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    UIutils.disErr(HomeActivity.this, error);
                                }
                            });
        }
    }


    @Override
    public void onNaviItemClick(int id) {
        Intent intent;
        switch (id) {
            case 0://login
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case 2:
                startActivity(new Intent(this, DetailedActivity.class));
                break;

            case 4:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case 5://set theme


                ThemeUtils.switchTheme(this, ThemeUtils.THEME_DARK);
                recreate();
                break;

            case 6://settings
                //startActivity(new Intent(this, SettingsActivity.class));
                ThemeUtils.switchTheme(this, ThemeUtils.THEME_LIGHT);
                recreate();
                break;


        }
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
        }
    }


    private void trySyncTab() {

        fragments.add(FeedFragment.newInstance("学院风采", Constant.TYPE_COLLEGE));
        fragments.add(FeedFragment.newInstance("青春通信", Constant.TYPE_YOUTH));
        fragments.add(FeedFragment.newInstance("科技创新", Constant.TYPE_SCIENTIFIC));
        fragments.add(FeedFragment.newInstance("通信校友", Constant.TYPE_CLASSMATE));
        if (ThemeUtils.isNightMode(this)){
            mTabLayout.setBackgroundColor(getResources().getColor(R.color.primary_night));
        } else {
            mTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        }
        mTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(fragments.size());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setViewPager(mViewPager);


    }

    private void startIntroAnim() {
        int actionbarSize = ScreenUtils.dpToPx(56);
        mToolbar.setTranslationY(-actionbarSize);
        actionView.getActionView().setTranslationY(-actionbarSize);
        mToolbar.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(300);
        actionView.getActionView().animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(600).start();
        mTabLayout.setTranslationY(-mToolbar.getHeight() - mTabLayout.getHeight());
        mTabLayout.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(700).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                trySyncTab();
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        actionView = menu.findItem(R.id.action_weather);
        actionView.setActionView(R.layout.include_weather);
        //trySyncTab();
        if (isAnimate) {
            isAnimate = false;
            startIntroAnim();
        }

        return super.onCreateOptionsMenu(menu);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //recreate();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        //recreate();
    }
}