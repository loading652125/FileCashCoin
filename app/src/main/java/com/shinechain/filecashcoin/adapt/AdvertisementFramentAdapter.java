package com.shinechain.filecashcoin.adapt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjs on 2018/11/24 11:30
 * Describe:
 */
public class AdvertisementFramentAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments = null;
	private  List<String> mFragmentTitles = null;
	private FragmentManager mFragmentManager;

	public AdvertisementFramentAdapter(FragmentManager fm) {
		super(fm);
		mFragments = new ArrayList<>();
		mFragmentTitles = new ArrayList<>();
		mFragmentManager = fm;
	}

	public void addFragment(Fragment fragment, String title) {
		mFragments.add(fragment);
		mFragmentTitles.add(title);
	}

	@Override
	public Fragment getItem(int position) {
		Log.v("AdFramentAdapter","getItem");
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitles.get(position);
	}
}
