package com.shinechain.filecashcoin.fragment.ad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinechain.filecashcoin.R;

/**
 * Created by zjs on 2018/11/23 14:39
 * Describe:
 */
public class AllADFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.e("AllADFragment","onCreateView");
		return inflater.inflate(R.layout.fragment_all_ad,container,false);
	}
}
