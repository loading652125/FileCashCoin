package com.shinechain.filecashcoin.fragment.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shinechain.filecashcoin.R;

/**
 * Created by zjs on 2018/12/04 15:22
 * Describe:
 */
public class EmailRegisterFragment extends Fragment {

    public static final String TAG = EmailRegisterFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register_email,container,false);

    }
}
