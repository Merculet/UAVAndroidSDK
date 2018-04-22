package io.merculet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.merculet.Session;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session.onPause(this);
    }
}
