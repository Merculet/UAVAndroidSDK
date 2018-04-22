package io.merculet;

import android.os.Bundle;
import android.view.View;

import io.merculet.example.R;


public class OtherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
    }

    public void tvClickOther(View view) {
        finish();
    }
}
