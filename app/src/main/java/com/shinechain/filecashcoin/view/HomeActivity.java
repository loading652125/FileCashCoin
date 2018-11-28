package com.shinechain.filecashcoin.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.common.BottomNavigationViewHelper;
import com.shinechain.filecashcoin.fragment.AdvertisementFragment;
import com.shinechain.filecashcoin.fragment.DealFragment;
import com.shinechain.filecashcoin.fragment.OrderFragment;
import com.shinechain.filecashcoin.fragment.SelfFragment;

public class HomeActivity extends AppCompatActivity {

	private long firstKeyDown = 0l;
	private BottomNavigationView navigation;
	private MenuItem menuItem;
	//fragment
	private DealFragment mDealFragment;
	private AdvertisementFragment mAdvertisementFragment;
	private OrderFragment mOrderFragment;
	private SelfFragment mSelfFragment;
	private Fragment mCurrentFragment;
	//页面标识
	private int mCurrentUIIndex = 0;
	private static final int DEAL_UI = 0;
	private static final int ADVERTISEMENT_UI = 1;
	private static final int ORDER_UI = 2;
	private static final int SELF_UI = 3;



	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.navigation_deal:
					mCurrentUIIndex = DEAL_UI;
					updateUI();
					return true;
				case R.id.navigation_advertisement:
					mCurrentUIIndex = ADVERTISEMENT_UI;
					updateUI();
					return true;
				case R.id.navigation_order:
					mCurrentUIIndex = ORDER_UI;
					updateUI();
					return true;
				case R.id.navigation_self:
					mCurrentUIIndex = SELF_UI;
					updateUI();
					return true;

			}
			return false;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		//状态栏操作
		StatusBarUtil.setColor(this,0xffFFAC1C,0);
		navigation = (BottomNavigationView) findViewById(R.id.navigation);
		//禁用图案缩放
		BottomNavigationViewHelper.disableShiftMode(navigation);
		//禁用文字缩放
		BottomNavigationViewHelper.disableItemScale(navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
	}

	//双击退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - firstKeyDown) > 2000) {
				Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
				firstKeyDown = System.currentTimeMillis();
			} else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					finishAffinity();
				} else {
					finish();
				}
				overridePendingTransition(0, R.anim.out_to_bottom);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void updateUI() {

		switch (mCurrentUIIndex) {
			case DEAL_UI:
				if (mDealFragment == null) {
					mDealFragment = new DealFragment();
				}
				switchFragment(mDealFragment);
				break;
			case ADVERTISEMENT_UI:
				if (mAdvertisementFragment == null) {
					mAdvertisementFragment = new AdvertisementFragment();

				}
				switchFragment(mAdvertisementFragment);
				break;
			case ORDER_UI:
				if (mOrderFragment == null) {
					mOrderFragment = new OrderFragment();
				}
				switchFragment(mOrderFragment);
				break;
			case SELF_UI:
				if (mSelfFragment == null) {
					mSelfFragment = new SelfFragment();
				}
				switchFragment(mSelfFragment);
				break;
		}
	}

	private void switchFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction;
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (mCurrentFragment != null) {
			fragmentTransaction.hide(mCurrentFragment);
		}
		if (fragment.isAdded()) {
			fragmentTransaction.show(fragment);
		} else {
			fragmentTransaction.add(R.id.content, fragment);
		}
		fragmentTransaction.commit();
		mCurrentFragment = fragment;
	}

}
