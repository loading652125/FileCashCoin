package com.shinechain.filecashcoin.view;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.adapt.BaseViewPagerAdapter;
import com.shinechain.filecashcoin.fragment.register.EmailRegisterFragment;
import com.shinechain.filecashcoin.fragment.register.PhoneRegisterFragment;

public class Register2Activity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BaseViewPagerAdapter baseViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        StatusBarUtil.setColor(this,0xffFFAC1C,50);
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.registerTabLayout);
        viewPager = findViewById(R.id.registerViewPager);
        baseViewPagerAdapter = new BaseViewPagerAdapter(getSupportFragmentManager());
        baseViewPagerAdapter.addFragment(new PhoneRegisterFragment(),getResources().getString(R.string.phone_register));
        baseViewPagerAdapter.addFragment(new EmailRegisterFragment(),getResources().getString(R.string.email_register));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(baseViewPagerAdapter);
    }
}
