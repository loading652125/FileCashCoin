package com.shinechain.filecashcoin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shinechain.filecashcoin.view.LoginActivity;
import com.shinechain.filecashcoin.view.RegisterActivity;
import com.shinechain.filecashcoin.view.ResetPasswordActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,ResetPasswordActivity.class);
        startActivity(intent);
    }
}
