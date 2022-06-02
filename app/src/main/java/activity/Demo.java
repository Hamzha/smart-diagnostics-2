package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.smart.agriculture.solutions.vechicle.vehicletracker.R;

import adaptor.CustomPagerAdapter;
import util.BaseClass;
import util.SharedPrefConst;
import util.SharedPreferenceHelper;

public class Demo extends BaseClass {
    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_demo);
        ToolbarnoNavigation(context, "Demo Pictures");
        TabLayout tabLayout = findViewById(R.id.tabDots);


        if (!SharedPreferenceHelper.getSharedPreferenceString(getApplication(), SharedPrefConst.LANGUAGE_PREF_FILE, SharedPrefConst.FIRST_TIME, "first").equals("first")) {
            langSelector(context);
            finish();
            startActivity(new Intent(context, SplashScreen.class));
        }

        final Button next = findViewById(R.id.new_next);
        final Button pre = findViewById(R.id.new_pre);
        final ViewPager viewPager = findViewById(R.id.viewpager);
        final CustomPagerAdapter adapter = new CustomPagerAdapter(this);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    pre.setVisibility(View.GONE);
                } else {
                    pre.setVisibility(View.VISIBLE);
                }
                if (position == 29) {
                    next.setText("Go");
                } else
                    next.setText("Next");
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        next.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 29)
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            else if (viewPager.getCurrentItem() == 29) {
                finish();

                startActivity(new Intent(context, SplashScreen.class));

            }
        });

        pre.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 0)
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        });


    }

    public void skipDemo(View view) {
        finish();
        startActivity(new Intent(context, SplashScreen.class));
    }
}
