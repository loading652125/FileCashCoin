package com.shinechain.filecashcoin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shinechain.filecashcoin.fragment.register.PhoneRegisterFragment;
import com.shinechain.filecashcoin.view.LoginActivity;
import com.shinechain.filecashcoin.view.RegisterEmailActivity;
import com.shinechain.filecashcoin.view.RegisterPhoneActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,RegisterPhoneActivity.class);
        startActivity(intent);
    }
}
