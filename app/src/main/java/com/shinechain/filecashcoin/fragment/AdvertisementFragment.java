package com.shinechain.filecashcoin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinechain.filecashcoin.R;
import com.shinechain.filecashcoin.adapt.AdvertisementFramentAdapter;
import com.shinechain.filecashcoin.fragment.ad.AllADFragment;
import com.shinechain.filecashcoin.fragment.ad.UseADFragment;

/**
 * Created by zjs on 2018/11/23 14:39
 * Describe: 广告页面
 */
public class AdvertisementFragment extends Fragment {
	private static final String TAG = "AdvertisementFragment";

	private ViewPager adViewpager;
	private TabLayout tabLayout;
	private AdvertisementFramentAdapter advertisementFramentAdapter;
    private View mView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.e(TAG,"onCreateView");
        mView = inflater.inflate(R.layout.fragment_advertisement,container,false);
		return mView;

	}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated");
        initViewPage();
    }


	//初始viewpager的适配器和监听事件
	private void initViewPage() {
		//View adView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_advertisement, null);
		tabLayout = mView.findViewById(R.id.tab_layout);
		adViewpager = mView.findViewById(R.id.view_pager);
		//需要通过getChildFragmentManager()来设置FragmentManager，而不能用getFragmentManager()
		advertisementFramentAdapter = new AdvertisementFramentAdapter(this.getFragmentManager());
        adViewpager.setAdapter(advertisementFramentAdapter);
		advertisementFramentAdapter.addFragment(new AllADFragment(),"全部");
		advertisementFramentAdapter.addFragment(new UseADFragment(),"已上架");
        advertisementFramentAdapter.addFragment(new AllADFragment(),"已下架");
        advertisementFramentAdapter.addFragment(new UseADFragment(),"已过期");
        advertisementFramentAdapter.notifyDataSetChanged();
		//Log.e(TAG,"onActivityCreated run");
		tabLayout.setupWithViewPager(adViewpager,true);
		adViewpager.setCurrentItem(0);
        Log.e(TAG,"initViewPage");

	}
}
