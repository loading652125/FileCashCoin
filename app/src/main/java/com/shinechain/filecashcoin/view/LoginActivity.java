package com.shinechain.filecashcoin.view;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jaeger.library.StatusBarUtil;
import com.shinechain.filecashcoin.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StatusBarUtil.setColor(this, 100,0);
        StatusBarUtil.setLightMode(this);
        Button forgetButton = findViewById(R.id.foget_password_button);
        forgetButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetButton.getPaint().setAntiAlias(true);
    }
}
